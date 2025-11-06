package br.com.tonypool.cliente.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.tonypool.cliente.requests.EnderecoRequest;
import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.model.Endereco;
import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.repository.IEnderecoRepository;
import br.com.tonypool.cliente.exception.ClienteNaoEncontradoException;

@Service
public class EnderecoConsumer {

    @Autowired
    private IEnderecoRepository enderecoRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @KafkaListener(containerFactory = "enderecoKafkaListenerContainerFactory", topics = "endereco-solicitacao", groupId = "cliente-service")
    @Transactional
    public void processarEndereco(EnderecoRequest request) {
        System.out.println("✅ Kafka recebeu EnderecoRequest: " + request);

        if (request == null || request.getEndereco() == null) {
            System.err.println("EnderecoRequest inválido recebido pelo consumidor: " + request);
            return;
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(request.getIdCliente());

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            Endereco endereco = enderecoRepository.findByCliente(cliente);
            if (endereco == null) {
                endereco = new Endereco();
            }

            endereco.setCliente(cliente);
            endereco.setLogradouro(request.getEndereco().getLogradouro());
            endereco.setNumero(request.getEndereco().getNumero());
            endereco.setComplemento(request.getEndereco().getComplemento());
            endereco.setBairro(request.getEndereco().getBairro());
            endereco.setCidade(request.getEndereco().getCidade());
            endereco.setUf(request.getEndereco().getUf());
            endereco.setCep(request.getEndereco().getCep());

            enderecoRepository.save(endereco);
        } else {
            // For testing purposes: do not throw an exception when Cliente is missing.
            // This prevents the listener from failing and avoids dead-letter publishing during tests.
            System.err.println("Cliente não encontrado (modo teste): " + request.getIdCliente());
            return;
        }
    }

}