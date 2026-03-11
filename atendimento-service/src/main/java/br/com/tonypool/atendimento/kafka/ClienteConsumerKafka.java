package br.com.tonypool.atendimento.kafka;

import br.com.tonypool.atendimento.requests.ClienteRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteConsumerKafka {

    @KafkaListener(topics = "cliente-consulta", groupId = "grupo-atendimento")
    public void consumirCliente(ClienteRequest cliente) {
        System.out.println("ClienteRequest consumido: " + cliente);
    }
}
