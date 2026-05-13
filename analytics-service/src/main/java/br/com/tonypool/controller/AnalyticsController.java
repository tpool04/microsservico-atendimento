package br.com.tonypool.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonypool.dto.AnalyticsDTO;
import br.com.tonypool.dto.ClienteServicoAnalyticsDTO;
import br.com.tonypool.repository.AnalyticsRepository;
import br.com.tonypool.service.ClienteServicoAnalyticsService;
import br.com.tonypool.service.ProfissionalAnalyticsService;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsRepository repository;
    private final ProfissionalAnalyticsService profissionalanalyticsservice;
    private final ClienteServicoAnalyticsService clienteServicoAnalyticsService;

    public AnalyticsController(AnalyticsRepository repository, ClienteServicoAnalyticsService clienteServicoAnalyticsService, ProfissionalAnalyticsService profissionalanalyticsservice) {
        this.repository = repository;
        this.profissionalanalyticsservice = profissionalanalyticsservice;
        this.clienteServicoAnalyticsService = clienteServicoAnalyticsService;
    }

    @GetMapping("/profissionais")
    public List<AnalyticsDTO> listar() {
        return repository.findAll()
                .stream()
                .map(a -> new AnalyticsDTO(
                        a.getProfissionalId(),
                        a.getProfissionalNome(),
                        a.getTotalAtendimentos()
                ))
                .toList();
    }

    @GetMapping("/dashboard")
    public List<AnalyticsDTO> dashboard() {
        return profissionalanalyticsservice.listarProfissionais(); 
    }
    
    @GetMapping("/clientes/servicos")
    public List<ClienteServicoAnalyticsDTO> listarTodosClientesComServicos() {
        return clienteServicoAnalyticsService.listarTodos()
                .stream()
                .map(a -> new ClienteServicoAnalyticsDTO(
                        a.getClienteId(),
                        a.getClienteNome(),
                        a.getServicoId(),
                        a.getServicoTipo(),
                        a.getDataAtendimento(),
                        a.getTotalExecutado()
                ))
                .toList();
    }
}
