package br.com.tonypool.cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.responses.ClienteResponse;
import br.com.tonypool.cliente.requests.ClienteRequest;
import br.com.tonypool.cliente.dto.ClienteDTO;

@Service
public class ClienteConsumer {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, ClienteResponse> kafkaTemplate;

    // Use the typed container factory so JsonDeserializer converts directly to ClienteRequest
    @KafkaListener(containerFactory = "clienteKafkaListenerContainerFactory", topics = "cliente-solicitacao", groupId = "cliente-service")
    public void processarSolicitacao(ClienteRequest request) {
        ClienteDTO clienteDTO = null;

        if ("ID".equals(request.getTipoConsulta())) {
            clienteDTO = clienteRepository.findById(Integer.parseInt(request.getValor()))
                .map(ClienteDTO::fromEntity)
                .orElse(null);
        } else if ("CPF".equals(request.getTipoConsulta())) {
            clienteDTO = clienteRepository.findByCpf2(request.getValor())
                .map(ClienteDTO::fromEntity)
                .orElse(null);
        }

        ClienteResponse response = new ClienteResponse();
        response.setCliente(clienteDTO);
        response.setCorrelationId(request.getCorrelationId());

        if (kafkaTemplate != null) {
            kafkaTemplate.send("cliente-resposta", response);
        } else {
            System.out.println("KafkaTemplate não disponível - pulando envio de ClienteResponse no contexto de teste.");
        }
    }
}