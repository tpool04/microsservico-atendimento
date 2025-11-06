package br.com.tonypool.auth.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import br.com.tonypool.auth.requests.EnderecoRequest;
import br.com.tonypool.auth.dto.EnderecoDTO;

@Service
public class EnderecoProducer {

    @Autowired
    private KafkaTemplate<String, EnderecoRequest> kafkaTemplate;

    public void enviarEndereco(Integer idCliente, EnderecoDTO enderecoDTO) {
        EnderecoRequest request = new EnderecoRequest();
        request.setIdCliente(idCliente);
        request.setEndereco(enderecoDTO);
        request.setCorrelationId(UUID.randomUUID().toString());

        kafkaTemplate.send("endereco-solicitacao", request);
    }
}

