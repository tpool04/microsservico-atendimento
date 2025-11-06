package br.com.tonypool.auth.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.tonypool.auth.client.ClienteServiceClient;
import br.com.tonypool.auth.dto.EnderecoDTO;
import br.com.tonypool.auth.helpers.MD5Helper;
import br.com.tonypool.auth.model.Cliente;
import br.com.tonypool.auth.repository.IClienteRepository;
import br.com.tonypool.auth.repository.IPerfilRepository;
import br.com.tonypool.auth.requests.CriarContaPostRequest;
import br.com.tonypool.auth.service.EnderecoProducer;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api")
public class CriarContaController {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IPerfilRepository perfilRepository;

    @Autowired
    private ClienteServiceClient clienteServiceClient;
    
    @Autowired
    private EnderecoProducer enderecoProducer;

    // Default: true = send via Kafka. Set to false for tests to call ClienteServiceClient directly.
    @Value("${app.endereco.via-kafka:true}")
    private boolean enderecoViaKafka;

    @ApiOperation("Endpoint para cadastro de conta do cliente.")
    @PostMapping("/criar-conta")
    public ResponseEntity<String> post(@RequestBody CriarContaPostRequest request) {
        try {
            // Salva o cliente e comita a transação
            Cliente cliente = salvarCliente(request);

            // Monta o endereço
            EnderecoDTO enderecoDTO = montarEnderecoDTO(request);

            if (enderecoViaKafka) {
                enderecoProducer.enviarEndereco(cliente.getIdCliente(), enderecoDTO);
            } else {
                // Chamada direta ao cliente-service para testes (garante que o cliente exista lá)
                clienteServiceClient.vincularEndereco(cliente.getIdCliente().longValue(), enderecoDTO);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Parabéns! Sua conta foi criada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno. Tente novamente mais tarde.");
        }
    }

    @Transactional
    public Cliente salvarCliente(CriarContaPostRequest request) throws Exception {
        if (clienteRepository.findByCpf(request.getCpf()) != null)
            throw new IllegalArgumentException("O CPF informado já está cadastrado.");

        if (clienteRepository.findByEmail(request.getEmail()) != null)
            throw new IllegalArgumentException("O email informado já está cadastrado.");

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setSenha(MD5Helper.encrypt(request.getSenha()));
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());

        cliente.setPerfil(perfilRepository.findById(2)
            .orElseThrow(() -> new RuntimeException("Perfil USER (id=2) não encontrado.")));

        return clienteRepository.saveAndFlush(cliente); // garante commit imediato
    }

    private EnderecoDTO montarEnderecoDTO(CriarContaPostRequest request) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setLogradouro(request.getLogradouro());
        dto.setNumero(request.getNumero());
        dto.setComplemento(request.getComplemento());
        dto.setBairro(request.getBairro());
        dto.setCidade(request.getCidade());
        dto.setUf(request.getUf());
        dto.setCep(request.getCep());
        return dto;
    }
}