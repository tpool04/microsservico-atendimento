package br.com.tonypool.cliente.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.model.Endereco;
import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.repository.IEnderecoRepository;
import br.com.tonypool.cliente.requests.EnderecoRequest;
import br.com.tonypool.cliente.dto.EnderecoDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.core.ProducerFactory;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1, topics = { "endereco-solicitacao" })
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "kafka.listener.auto-startup=true",
        // Provide base URL for AtendimentoServiceClient used by the application context during tests
        "atendimento-service.url=http://localhost",
        // Prevent Spring from auto-configuring Web MVC (avoids No ServletContext set when webEnvironment=NONE)
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration"
})
public class EnderecoConsumerIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IClienteRepository clienteRepository;

    @MockBean
    private IEnderecoRepository enderecoRepository;

    @Test
    public void deveConsumirMensagemDeEnderecoETestarProcessamento() throws Exception {
        // Arrange: mock repository to return a cliente
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Maria");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.findById(cliente.getIdCliente())).thenReturn(Optional.empty());

        // Producer properties to connect to embedded Kafka
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(pf, true);
        kafkaTemplate.setDefaultTopic("endereco-solicitacao");

        // Create payload
        EnderecoDTO dto = new EnderecoDTO();
        dto.setLogradouro("Rua Teste");
        dto.setNumero("123");
        dto.setComplemento("Apt");
        dto.setBairro("Centro");
        dto.setCidade("Cidade");
        dto.setUf("SP");
        dto.setCep("00000-000");

        EnderecoRequest request = new EnderecoRequest();
        request.setIdCliente(1);
        request.setEndereco(dto);
        request.setCorrelationId("corr-1");

        String payload = objectMapper.writeValueAsString(request);

        // Act: send message
        kafkaTemplate.sendDefault(payload);
        kafkaTemplate.flush();

        // Assert: wait until consumer processed and saved endereco
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(enderecoRepository, times(1)).save(any(Endereco.class))
        );
    }
}