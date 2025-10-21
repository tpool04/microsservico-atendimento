package br.com.tonypool.atendimento.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonypool.atendimento.dto.ClienteDTO;
import br.com.tonypool.atendimento.model.Atendimento;

import br.com.tonypool.atendimento.model.Profissional;
import br.com.tonypool.atendimento.model.Servico;
import br.com.tonypool.atendimento.repository.IAtendimentoRepository;
import br.com.tonypool.atendimento.client.ClienteServiceClient;

import br.com.tonypool.atendimento.repository.IProfissionalRepository;
import br.com.tonypool.atendimento.repository.IServicoRepository;
import br.com.tonypool.atendimento.requests.AtendimentoPostRequest;
import br.com.tonypool.atendimento.requests.ReagendarAtendimentoRequest;
import br.com.tonypool.atendimento.responses.AtendimentoGetResponse;
import br.com.tonypool.atendimento.responses.ErrorResponse;
import br.com.tonypool.atendimento.security.TokenSecurity;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentosController {

	@Autowired
	private IAtendimentoRepository atendimentoRepository;

	@Autowired
	private IServicoRepository servicoRepository;
	
	@Autowired
    private ClienteServiceClient clienteServiceClient;

	@Autowired
	private IProfissionalRepository profissionalRepository;

	private static final Logger logger = LoggerFactory.getLogger(AtendimentosController.class);

	@ApiOperation("Endpoint para o cliente cadastrar um atendimento.")
	@PostMapping
	public ResponseEntity<String> cadastrarAtendimento(
	        @RequestBody AtendimentoPostRequest request,
	        @RequestHeader("Authorization") String authorization) {

	    try {
	        // Extrair ID do cliente do token JWT
	        String token = authorization.replace("Bearer ", "");
	        Integer idCliente = TokenSecurity.getIdClienteFromToken(token);
	        if (idCliente == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido ou expirado.");
	        }

	        // Buscar serviço
	        Servico servico = servicoRepository.findById(request.getIdServico())
	                .orElse(null);
	        if (servico == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Serviço não encontrado.");
	        }

	        // Buscar profissional
	        Profissional profissional = profissionalRepository.findById(request.getIdProfissional())
	                .orElse(null);
	        if (profissional == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Profissional não encontrado.");
	        }

	        // Verificar se o profissional realiza o serviço
	        boolean profissionalValido = profissional.getServicos().stream()
	                .anyMatch(s -> s.getIdServico().equals(servico.getIdServico()));
	        if (!profissionalValido) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Erro: O profissional não realiza o serviço desejado.");
	        }

	        // Criar data/hora do atendimento
	        Date dataHora = new SimpleDateFormat("dd/MM/yyyy-HH:mm")
	                .parse(request.getData() + "-" + request.getHora());

	        // Criar e salvar atendimento
	        Atendimento atendimento = new Atendimento();
	        atendimento.setIdCliente(idCliente);
	        atendimento.setServico(servico);
	        atendimento.setProfissional(profissional);
	        atendimento.setDataHora(dataHora);
	        atendimento.setObservacoes(request.getObservacoes());

	        atendimentoRepository.save(atendimento);

	        return ResponseEntity.status(HttpStatus.CREATED).body("Atendimento cadastrado com sucesso.");
	    } catch (ParseException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao interpretar data/hora.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + e.getMessage());
	    }
	}


	@ApiOperation(value = "Endpoint para consulta de atendimentos do cliente autenticado.",
		    notes = "Retorna 200 com lista, 204 se vazio, 401 se não autenticado.")
		@GetMapping("/cliente")
		public ResponseEntity<?> consultarAtendimentosDoCliente(@RequestHeader("Authorization") String authorization) {
		    try {
		        if (authorization == null || !authorization.startsWith("Bearer ")) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                .body(new ErrorResponse("Token ausente ou malformado."));
		        }

		        String token = authorization.replace("Bearer ", "").trim();
		        Integer idCliente = TokenSecurity.getIdClienteFromToken(token);
		        if (idCliente == null) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                .body(new ErrorResponse("Token inválido ou cliente não identificado."));
		        }

		        List<Atendimento> atendimentos = atendimentoRepository.findByIdCliente(idCliente);
		        if (atendimentos == null || atendimentos.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		        }

		        List<AtendimentoGetResponse> lista = new ArrayList<>();
		        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		        for (Atendimento atendimento : atendimentos) {
		            AtendimentoGetResponse response = new AtendimentoGetResponse();
		            response.setIdAtendimento(atendimento.getIdAtendimento());
		            response.setDataHora(formatter.format(atendimento.getDataHora()));
		            response.setNomeServico(atendimento.getServico().getNome());
		            response.setValorServico(atendimento.getServico().getValor());
		            response.setNomeProfissional(atendimento.getProfissional().getNome());
		            response.setTelefoneProfissional(atendimento.getProfissional().getTelefone());
		            response.setObservacoes(atendimento.getObservacoes());
		            lista.add(response);
		        }

		        return ResponseEntity.ok(lista);
		    } catch (SecurityException se) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		            .body(new ErrorResponse("Erro de autenticação."));
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(new ErrorResponse("Erro interno ao consultar atendimentos."));
		    }
		}


	private ClienteDTO getClienteByAccessToken(HttpServletRequest httpRequest) throws Exception {
	    String accessToken = httpRequest.getHeader("Authorization");
	    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
	        throw new Exception("Token JWT ausente ou malformado.");
	    }

	    accessToken = accessToken.replace("Bearer ", "").trim();
	    String cpf = TokenSecurity.getUserFromToken(accessToken);
	    if (cpf == null || cpf.isEmpty()) {
	        throw new Exception("CPF não encontrado no token.");
	    }

	    ClienteDTO cliente = clienteServiceClient.buscarPorCpf(cpf);
	    if (cliente == null) {
	        throw new Exception("Cliente não encontrado para o CPF extraído do token.");
	    }

	    return cliente;
	}


	@ApiOperation("Endpoint para reagendar um atendimento.")
	@PutMapping("/reagendar/{id}")
	public ResponseEntity<String> reagendar(@PathVariable Integer id, @RequestBody ReagendarAtendimentoRequest request) {
		try {
			Atendimento atendimento = atendimentoRepository.findById(id)
					.orElseThrow(() -> new Exception("Atendimento não encontrado."));

			Profissional novoProfissional = atendimento.getProfissional();
			Servico novoServico = atendimento.getServico();

			if (request.getIdProfissional() != null) {
				novoProfissional = profissionalRepository.findById(request.getIdProfissional())
						.orElseThrow(() -> new Exception("Profissional não encontrado."));
			}

			if (request.getIdServico() != null) {
				novoServico = servicoRepository.findById(request.getIdServico())
						.orElseThrow(() -> new Exception("Serviço não encontrado."));
			}

			final Servico servicoParaVerificar = novoServico;
			List<Servico> servicosDoProfissional = novoProfissional.getServicos();
			logger.info("Serviços do profissional: {}", servicosDoProfissional.stream().map(Servico::getIdServico).toList());
			logger.info("Serviço solicitado: {}", servicoParaVerificar.getIdServico());
			boolean profissionalValido = servicosDoProfissional.stream()
					.anyMatch(s -> s.getIdServico().equals(servicoParaVerificar.getIdServico()));
			if (!profissionalValido) {
				logger.warn("Tentativa de reagendar para profissional que não faz o serviço. Atendimento id: {}, Profissional id: {}, Serviço id: {}", id, novoProfissional.getIdProfissional(), servicoParaVerificar.getIdServico());
				String mensagemErro = String.format(
                    "Não é possível reagendar: o profissional selecionado '%s' não está habilitado para realizar o serviço '%s'.",
                    novoProfissional.getNome(),
                    servicoParaVerificar.getNome()
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mensagemErro);
			}

			// Só altera o atendimento após a validação
			atendimento.setProfissional(novoProfissional);
			atendimento.setServico(novoServico);
			atendimento.setObservacoes(request.getNovaObservacao());

			if (request.getNovaDataHora() != null) {
				Date novaDataHora = new SimpleDateFormat("dd/MM/yyyy-HH:mm").parse(request.getNovaDataHora());
				atendimento.setDataHora(novaDataHora);
			}

			atendimentoRepository.save(atendimento);
			return ResponseEntity.ok("Atendimento reagendado com sucesso.");
		} catch (Exception e) {
			logger.error("Erro ao reagendar atendimentosss: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@ApiOperation("Endpoint para cancelar um atendimento.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> cancelar(@PathVariable Integer id) {
		try {
			Atendimento atendimento = atendimentoRepository.findById(id)
					.orElseThrow(() -> new Exception("Atendimento não encontrado."));
			atendimentoRepository.delete(atendimento);
			return ResponseEntity.ok("Atendimento cancelado com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@ApiOperation("Endpoint para o cliente consultar um atendimento específico pelo ID.")
	@GetMapping("/{id}")
	public ResponseEntity<?> getAtendimentoById(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
	    try {
	        // Extrair ID do cliente do token JWT
	        String token = authHeader.replace("Bearer ", "").trim();
	        Integer idCliente = TokenSecurity.getIdClienteFromToken(token);
	        if (idCliente == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou cliente não identificado.");
	        }

	        // Buscar atendimento
	        Atendimento atendimento = atendimentoRepository.findById(id).orElse(null);
	        if (atendimento == null || atendimento.getIdCliente() == null) {
	            logger.warn("Atendimento não encontrado ou sem cliente associado: {}", id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Atendimento não encontrado ou sem cliente associado.");
	        }

	        // Verificar se o atendimento pertence ao cliente autenticado
	        if (!atendimento.getIdCliente().equals(idCliente)) {
	            logger.warn("Acesso negado: atendimento pertence a outro cliente. Atendimento ID: {}, Cliente autenticado: {}", id, idCliente);
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: atendimento pertence a outro cliente.");
	        }

	        // Buscar dados do cliente via Feign
	        ClienteDTO cliente = clienteServiceClient.buscarPorId(idCliente);

	        // Montar resposta
	        AtendimentoGetResponse response = new AtendimentoGetResponse();
	        response.setIdAtendimento(atendimento.getIdAtendimento());
	        response.setDataHora(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(atendimento.getDataHora()));
	        response.setNomeServico(atendimento.getServico().getNome());
	        response.setValorServico(atendimento.getServico().getValor());
	        response.setNomeProfissional(atendimento.getProfissional().getNome());
	        response.setTelefoneProfissional(atendimento.getProfissional().getTelefone());
	        response.setNomeCliente(cliente.getNome());
	        response.setCpfCliente(cliente.getCpf());
	        response.setObservacoes(atendimento.getObservacoes());

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        logger.error("Erro ao consultar atendimento por ID: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao consultar atendimento.");
	    }
	}


	@ApiOperation("Endpoint para consultar atendimentos por id do cliente.")
    @PostMapping("/por-cliente")
    public ResponseEntity<Map<Integer, List<AtendimentoGetResponse>>> buscarPorClientes(@RequestBody List<Integer> ids) {
        try {
            Map<Integer, List<AtendimentoGetResponse>> resultado = new HashMap<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Integer id : ids) {
                List<Atendimento> atendimentos = atendimentoRepository.findByIdCliente(id);
                ClienteDTO clienteDTO = null;
                try {
                    clienteDTO = clienteServiceClient.buscarPorId(id);
                } catch (Exception e) {
                    clienteDTO = null;
                }
                List<AtendimentoGetResponse> dtoList = new ArrayList<>();
                for (Atendimento atendimento : atendimentos) {
                    AtendimentoGetResponse dto = new AtendimentoGetResponse(
                        atendimento.getIdAtendimento(),
                        formatter.format(atendimento.getDataHora()),
                        atendimento.getServico().getNome(),
                        atendimento.getServico().getValor(),
                        atendimento.getProfissional().getNome(),
                        atendimento.getProfissional().getTelefone(),
                        clienteDTO != null ? clienteDTO.getNome() : "",
                        clienteDTO != null ? clienteDTO.getCpf() : "",
                        atendimento.getObservacoes()
                    );
                    dtoList.add(dto);
                }
                resultado.put(id, dtoList);
            }
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/por-cliente/{id}")
    public ResponseEntity<List<Atendimento>> buscarAtendimentosPorCliente(@PathVariable("id") Integer idCliente) {
        try {
            List<Atendimento> atendimentos = atendimentoRepository.findByIdCliente(idCliente);
            return ResponseEntity.ok(atendimentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}