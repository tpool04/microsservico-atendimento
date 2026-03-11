package br.com.tonypool.atendimento.kafka;

import br.com.tonypool.atendimento.requests.ClienteRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClienteProducer {

    private final KafkaTemplate<String, ClienteRequest> kafkaTemplate;

    public ClienteProducer(KafkaTemplate<String, ClienteRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarCliente(ClienteRequest cliente) {
        kafkaTemplate.send("cliente-consulta", cliente.getCorrelationId(), cliente);
        System.out.println("✅ ClienteRequest enviado para o tópico: " + cliente);
    }
}
