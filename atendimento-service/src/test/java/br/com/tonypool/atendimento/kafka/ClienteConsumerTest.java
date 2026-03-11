package br.com.tonypool.atendimento.kafka;

import br.com.tonypool.atendimento.dto.ClienteDTO;
import br.com.tonypool.atendimento.requests.ClienteRequest;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "cliente-consulta" })
@DirtiesContext
class ClienteConsumerTest {

    @Autowired
    private KafkaTemplate<String, ClienteRequest> kafkaTemplate;

    @Test
    void deveConsumirClienteRequest() throws InterruptedException {
        ClienteRequest cliente = new ClienteRequest();
        cliente.setTipoConsulta("CPF");
        cliente.setValor("12345678900");
        cliente.setCorrelationId("abc-123");

        kafkaTemplate.send("cliente-consulta", cliente.getCorrelationId(), cliente);

        // Aguarda alguns segundos para o consumidor processar
        Thread.sleep(2000);

        // Aqui você pode verificar efeitos colaterais, logs ou mocks
        System.out.println("Mensagem enviada para o tópico cliente-consulta");
    }
}