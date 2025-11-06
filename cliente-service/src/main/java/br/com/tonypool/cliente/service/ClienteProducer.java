package br.com.tonypool.cliente.service;

import br.com.tonypool.cliente.dto.ClienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClienteProducer {

    @Autowired(required = false)
    private KafkaTemplate<String, ClienteDTO> kafkaTemplate;

    public ClienteProducer() {
        // Construtor sem argumentos para permitir criação do bean em contextos de teste
    }

    public void enviarClienteDTO(ClienteDTO clienteDTO) {
        if (kafkaTemplate == null) {
            System.out.println("KafkaTemplate não disponível - pulando envio de ClienteDTO no contexto de teste.");
            return;
        }

        // Define a chave como o CPF (pode ser qualquer identificador único)
        String chave = clienteDTO.getCpf();

        // Envia para o tópico "cliente-consulta"
        kafkaTemplate.send("cliente-consulta", chave, clienteDTO);
    }
}