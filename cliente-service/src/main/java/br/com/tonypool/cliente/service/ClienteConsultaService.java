package br.com.tonypool.cliente.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.tonypool.cliente.client.AtendimentoServiceClient;
import br.com.tonypool.cliente.dto.*;
import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.responses.AtendimentoGetResponse;

@Service
public class ClienteConsultaService {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private AtendimentoServiceClient atendimentoClient;
    
    @Autowired
    private AtendimentoServiceClient atendimentoServiceClient;

    public List<ClienteAtendimentosDTO> listarClientesComAtendimentos(String authorization) {
        List<ClienteAtendimentosDTO> resposta = new ArrayList<>();
        List<Cliente> clientes = clienteRepository.findAll();

        for (Cliente cliente : clientes) {
            ClienteDTO clienteDTO = new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIs2FAEnabled()
            );

            // Buscar atendimentos usando o método que aceita lista de IDs
            List<Integer> ids = new ArrayList<>();
            ids.add(cliente.getIdCliente());
            List<AtendimentoGetResponse> atendimentos = new ArrayList<>();
            try {
                Map<Integer, List<AtendimentoGetResponse>> resultado = atendimentoClient.buscarAtendimentosPorClientes(ids, authorization);
                if (resultado != null && resultado.containsKey(cliente.getIdCliente())) {
                    atendimentos = resultado.get(cliente.getIdCliente());
                }
            } catch (Exception e) {
                // Em caso de erro, mantém lista vazia
            }

            resposta.add(new ClienteAtendimentosDTO(clienteDTO, atendimentos));
        }

        return resposta;
    }
}