package br.com.tonypool.atendimento.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.tonypool.atendimento.requests.ClienteRequest;

import org.springframework.kafka.core.KafkaTemplate;



@Service
public class ClienteProducer {

    @Autowired
    private KafkaTemplate<String, ClienteRequest> kafkaTemplate;

    public void solicitarClientePorId(Integer id, String correlationId) {
        ClienteRequest request = new ClienteRequest();
        request.setTipoConsulta("ID");
        request.setValor(String.valueOf(id));
        request.setCorrelationId(correlationId);

        kafkaTemplate.send("cliente-solicitacao", request);
    }
    
    public void solicitarClientePorCpf(String cpf, String correlationId) {
        ClienteRequest request = new ClienteRequest();
        request.setTipoConsulta("CPF");
        request.setValor(cpf);
        request.setCorrelationId(correlationId);

        kafkaTemplate.send("cliente-solicitacao", request);
    }

}

