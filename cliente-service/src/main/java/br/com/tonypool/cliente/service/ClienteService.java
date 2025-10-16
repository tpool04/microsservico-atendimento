package br.com.tonypool.cliente.service;

import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.model.Endereco;
import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.repository.IEnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
	
	

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IEnderecoRepository enderecoRepository;

    public Cliente buscarPorCpf(String cpf) throws Exception {
        return clienteRepository.findByCpf(cpf);
    }

    public Endereco buscarEnderecoPorCliente(Cliente cliente) {
        return enderecoRepository.findByCliente(cliente);
    }

    public void atualizarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public void atualizarEndereco(Endereco endereco) {
        enderecoRepository.save(endereco);
    }

    public Iterable<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }
}

