package br.com.tonypool.atendimento.kafka;

import br.com.tonypool.atendimento.dto.ClienteDTO;
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
    private KafkaTemplate<String, ClienteDTO> kafkaTemplate;

    @Test
    void deveConsumirClienteDTO() throws InterruptedException {
        ClienteDTO cliente = new ClienteDTO(1, "Tonypool", "12345678900", "tony@example.com", "21999999999", true);

        kafkaTemplate.send("cliente-consulta", cliente.getCpf(), cliente);

        // Aguarda alguns segundos para o consumidor processar
        Thread.sleep(2000);

        // Aqui você pode verificar efeitos colaterais, logs ou mocks
        System.out.println("✅ Mensagem enviada para o tópico cliente-consulta");
    }
}