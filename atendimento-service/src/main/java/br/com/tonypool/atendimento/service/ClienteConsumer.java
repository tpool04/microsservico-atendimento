package br.com.tonypool.atendimento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import br.com.tonypool.atendimento.responses.ClienteResponse;
import br.com.tonypool.atendimento.dto.ClienteDTO;
import br.com.tonypool.atendimento.cache.ClienteCache;

@Service
public class ClienteConsumer {
	
	@Autowired
    private ClienteCache clienteCache;

	@KafkaListener(topics = "cliente-resposta", groupId = "atendimento-service")
    public void receberCliente(ClienteResponse response) {
		
		System.out.println("📥 Cliente recebido via Kafka:");
        System.out.println("CorrelationId: " + response.getCorrelationId());
        System.out.println("Cliente: " + response.getCliente());
		
        clienteCache.salvar(response.getCorrelationId(), response.getCliente());
    }
}

