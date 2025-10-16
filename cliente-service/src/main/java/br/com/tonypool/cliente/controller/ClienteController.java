package br.com.tonypool.cliente.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.tonypool.cliente.dto.ClienteCompletoDTO;
import br.com.tonypool.cliente.dto.ClienteDTO;
import br.com.tonypool.cliente.dto.ClienteAtendimentoDTO;

import br.com.tonypool.cliente.dto.EnderecoDTO;
import br.com.tonypool.cliente.client.AtendimentoServiceClient;
import br.com.tonypool.cliente.dto.ClienteAtendimentosDTO;
import br.com.tonypool.cliente.responses.AtendimentoGetResponse;
import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.model.Endereco;
import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.repository.IEnderecoRepository;
import br.com.tonypool.cliente.requests.AtualizarClienteRequest;
import br.com.tonypool.cliente.security.TokenSecurity;
import br.com.tonypool.cliente.service.ClienteService;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IEnderecoRepository enderecoRepository;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private AtendimentoServiceClient atendimentoServiceClient;

    // Endpoint autenticado: retorna dados do cliente autenticado
    @ApiOperation("Endpoint autenticado: retorna dados do cliente autenticado.")
    @GetMapping("/me")
    public ResponseEntity<ClienteCompletoDTO> consultarMeusDados(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            String cpf = TokenSecurity.getUserFromToken(token);
            Cliente cliente = clienteService.buscarPorCpf(cpf);
            if (cliente == null) {
                return ResponseEntity.status(404).build();
            }
            Endereco endereco = clienteService.buscarEnderecoPorCliente(cliente);
            ClienteDTO clienteDTO = new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIs2FAEnabled()
            );
            EnderecoDTO enderecoDTO = new EnderecoDTO(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep()
            );
            ClienteCompletoDTO resposta = new ClienteCompletoDTO(clienteDTO, enderecoDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint autenticado: atualiza dados do cliente autenticado
    @ApiOperation("Endpoint autenticado: atualiza dados do cliente autenticado.")
    @PutMapping("/me")
    public ResponseEntity<String> atualizarMeusDados(
        @RequestHeader("Authorization") String authorization,
        @RequestBody AtualizarClienteRequest request
    ) {
        try {
            String token = authorization.replace("Bearer ", "");
            String cpf = TokenSecurity.getUserFromToken(token);
            Cliente cliente = clienteService.buscarPorCpf(cpf);
            if (cliente == null) {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }
            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setTelefone(request.getTelefone());
            clienteRepository.save(cliente);
            Endereco endereco = clienteService.buscarEnderecoPorCliente(cliente);
            if (endereco != null) {
                endereco.setLogradouro(request.getLogradouro());
                endereco.setNumero(request.getNumero());
                endereco.setComplemento(request.getComplemento());
                endereco.setBairro(request.getBairro());
                endereco.setCidade(request.getCidade());
                endereco.setUf(request.getUf());
                endereco.setCep(request.getCep());
                enderecoRepository.save(endereco);
            }
           
            return ResponseEntity.ok("Dados atualizados com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar dados.");
        }
    }
    
    @ApiOperation("Vincula um endereço ao cliente pelo ID.")
    @PostMapping("/{id}/endereco")
    public ResponseEntity<String> vincularEnderecoAoCliente(
        @PathVariable("id") Long idCliente,
        @RequestBody EnderecoDTO enderecoDTO
    ) {
        try {
        	System.out.println("➡️ Recebido endereço para so o cliente1: " + (idCliente.intValue()));
        	Cliente cliente = clienteRepository.findByIdCliente(idCliente.intValue());
            if (cliente == null) {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }

            Endereco endereco = new Endereco();
            // Associa ao endereço
            endereco.setCliente(cliente);
            endereco.setLogradouro(enderecoDTO.getLogradouro());
            endereco.setNumero(enderecoDTO.getNumero());
            endereco.setComplemento(enderecoDTO.getComplemento());
            endereco.setBairro(enderecoDTO.getBairro());
            endereco.setCidade(enderecoDTO.getCidade());
            endereco.setUf(enderecoDTO.getUf());
            endereco.setCep(enderecoDTO.getCep());
            
            enderecoRepository.save(endereco);
            return ResponseEntity.ok("Endereço vinculado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao vincular endereço.");
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("lista todos os clientes com seus atendimentos e profissionais.")
    @GetMapping("/atendimentos-clientes")
    public ResponseEntity<List<ClienteAtendimentosDTO>> listarClientesComAtendimentos(@RequestHeader("Authorization") String authorization) {
        try {
            List<ClienteAtendimentosDTO> resposta = new ArrayList<>();
            Iterable<Cliente> clientes = clienteService.listarTodos();
            for (Cliente cliente : clientes) {
                ClienteDTO clienteDTO = new ClienteDTO(
                    cliente.getIdCliente(),
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getEmail(),
                    cliente.getTelefone(),
                    cliente.getIs2FAEnabled()
                );
                List<Integer> idList = new ArrayList<>();
                idList.add(cliente.getIdCliente());
                Map<Integer, List<AtendimentoGetResponse>> atendimentosMap =
                    atendimentoServiceClient.buscarAtendimentosPorClientes(idList, authorization);
                List<AtendimentoGetResponse> atendimentosDTO = atendimentosMap.getOrDefault(cliente.getIdCliente(), new ArrayList<>());
                resposta.add(new ClienteAtendimentosDTO(clienteDTO, atendimentosDTO));
            }
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            logger.error("Erro geral ao listar clientes com atendimentos: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @ApiOperation("Consulta um cliente pelo ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> consultarClientePorId(@PathVariable("id") Integer id) {
        try {
            Cliente cliente = clienteRepository.findByIdCliente(id);
            if (cliente == null) {
                return ResponseEntity.status(404).build();
            }
            ClienteDTO clienteDTO = new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIs2FAEnabled()
            );
            return ResponseEntity.ok(clienteDTO);
        } catch (Exception e) {
            logger.error("Erro ao consultar cliente por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @ApiOperation("Consulta um atendimento específico de um cliente pelo ID.")
    @GetMapping("/{idCliente}/atendimentos/{idAtendimento}")
    public ResponseEntity<ClienteAtendimentoDTO> consultarAtendimentoEspecificoDoCliente(
        @PathVariable("idCliente") Integer idCliente,
        @PathVariable("idAtendimento") Integer idAtendimento,
        @RequestHeader("Authorization") String authorization) {
        try {
            Cliente cliente = clienteRepository.findByIdCliente(idCliente);
            if (cliente == null) {
                return ResponseEntity.status(404).build();
            }
            AtendimentoGetResponse atendimento = atendimentoServiceClient.buscarAtendimentoPorId(idAtendimento, authorization);
            if (atendimento == null) {
                return ResponseEntity.status(404).build();
            }
            ClienteDTO clienteDTO = new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIs2FAEnabled()
            );
            ClienteAtendimentoDTO resposta = new ClienteAtendimentoDTO(clienteDTO, atendimento);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            logger.error("Erro ao consultar atendimento específico do cliente: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
