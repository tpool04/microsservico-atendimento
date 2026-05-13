package br.com.tonypool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tonypool.dto.AnalyticsDTO;
import br.com.tonypool.model.Analytics;
import br.com.tonypool.event.AtendimentoEvent;
import br.com.tonypool.repository.AnalyticsRepository;



@Service
public class AnalyticsService {

    private final ProfissionalAnalyticsService profissionalService;
    private final ClienteAnalyticsService clienteService;
    private final ServicoAnalyticsService servicoService;
    private final ClienteServicoAnalyticsService clienteServicoAnalyticsService;

    public AnalyticsService(
            ProfissionalAnalyticsService profissionalService,
            ClienteAnalyticsService clienteService,
            ServicoAnalyticsService servicoService,
            ClienteServicoAnalyticsService clienteServicoAnalyticsService) {

        this.profissionalService = profissionalService;
        this.clienteService = clienteService;
        this.servicoService = servicoService;
        this.clienteServicoAnalyticsService = clienteServicoAnalyticsService;
    }

    public void processarEvento(AtendimentoEvent event) {

        
        profissionalService.processar(event);

       
        clienteService.processar(event);

        
        servicoService.processar(event);
        
        clienteServicoAnalyticsService.processar(event);
    }
}
