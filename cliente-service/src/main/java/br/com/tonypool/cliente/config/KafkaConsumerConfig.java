package br.com.tonypool.cliente.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import br.com.tonypool.cliente.requests.EnderecoRequest;
import br.com.tonypool.cliente.requests.ClienteRequest;
import br.com.tonypool.cliente.exception.ClienteNaoEncontradoException;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Autowired(required = false)
    private KafkaProperties kafkaProperties;

    // KafkaTemplate opcional: pode não existir em contexto de teste
    @Autowired(required = false)
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.listener.auto-startup:true}")
    private boolean kafkaAutoStartup;

    private Map<String, Object> sanitizedProps() {
        Map<String, Object> props = (kafkaProperties != null)
                ? new HashMap<>(kafkaProperties.buildConsumerProperties())
                : new HashMap<>();

        // Provide sensible defaults when KafkaProperties is not available (e.g. in slice tests)
        props.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Remove Spring-specific Json deserializer properties to avoid conflicts when we provide a configured JsonDeserializer instance
        Set<String> keysToRemove = props.keySet().stream()
                .filter(k -> k.startsWith("spring.json") || k.startsWith("spring.deserializer") || k.startsWith("spring.kafka"))
                .collect(Collectors.toSet());
        for (String k : keysToRemove) {
            props.remove(k);
        }
        // Also remove the standard value deserializer property if present to avoid double-configuration
        props.remove("value.deserializer");
        props.remove("value-deserializer");

        return props;
    }

    @Bean
    public ConsumerFactory<String, EnderecoRequest> enderecoConsumerFactory() {
        Map<String, Object> props = sanitizedProps();

        // Create a typed JsonDeserializer via constructor/setters (do NOT leave spring.json.* props in the map)
        JsonDeserializer<EnderecoRequest> deserializer = new JsonDeserializer<>(EnderecoRequest.class, false);
        deserializer.addTrustedPackages("br.com.tonypool.*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EnderecoRequest> enderecoKafkaListenerContainerFactory(
            DefaultErrorHandler defaultErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, EnderecoRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(enderecoConsumerFactory());
        // Auto-start configurável via propriedade kafka.listener.auto-startup (default true)
        factory.setAutoStartup(this.kafkaAutoStartup);
        factory.setCommonErrorHandler(defaultErrorHandler);
        factory.setConsumerFactory(enderecoConsumerFactory());
        factory.setConcurrency(3); 
        return factory;
    }

    // New: typed consumer factory and listener container for ClienteRequest
    @Bean
    public ConsumerFactory<String, ClienteRequest> clienteConsumerFactory() {
        Map<String, Object> props = sanitizedProps();

        JsonDeserializer<ClienteRequest> deserializer = new JsonDeserializer<>(ClienteRequest.class, false);
        deserializer.addTrustedPackages("br.com.tonypool.*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean(name = "clienteKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ClienteRequest> clienteKafkaListenerContainerFactory(
            DefaultErrorHandler defaultErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, ClienteRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(clienteConsumerFactory());
        factory.setAutoStartup(this.kafkaAutoStartup);
        factory.setCommonErrorHandler(defaultErrorHandler);
        factory.setConsumerFactory(clienteConsumerFactory());
        factory.setConcurrency(3); 
        return factory;
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            DefaultErrorHandler defaultErrorHandler) {

        Map<String, Object> props = sanitizedProps();

        JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class, false);
        deserializer.addTrustedPackages("*");

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer));
        // Auto-start configurável via propriedade kafka.listener.auto-startup (default true)
        factory.setAutoStartup(this.kafkaAutoStartup);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        DefaultErrorHandler handler;

        // 3 retries (attempts after first failure) with 1s backoff
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);

        if (this.kafkaTemplate != null) {
            DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(this.kafkaTemplate,
                    (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
            handler = new DefaultErrorHandler(recoverer, backOff);
        } else {
            // Em contextos de teste sem KafkaTemplate, criar handler sem recoverer (apenas retries)
            handler = new DefaultErrorHandler(backOff);
        }

        // marcar a exceção de cliente não encontrado como não-retryable para ir direto para DLT quando houver recoverer
        handler.addNotRetryableExceptions(ClienteNaoEncontradoException.class);

        return handler;
    }
}