package br.com.tonypool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tonypool.dto.AnalyticsDTO;
import br.com.tonypool.model.Analytics;
import br.com.tonypool.event.AtendimentoEvent;
import br.com.tonypool.repository.AnalyticsRepository;

@Service
public class ProfissionalAnalyticsService {

    private final AnalyticsRepository repository;

    public ProfissionalAnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public void processar(AtendimentoEvent event) {
        Analytics analytics = repository.findByProfissionalId(event.getProfissionalId())
                .orElse(new Analytics(event.getProfissionalId(), event.getProfissionalNome(), 0));

        analytics.incrementarAtendimentos();
        repository.save(analytics);
    }

    public List<AnalyticsDTO> listarProfissionais() {
        return repository.findAll()
                .stream()
                .map(a -> new AnalyticsDTO(
                        a.getProfissionalId(),
                        a.getProfissionalNome(),
                        a.getTotalAtendimentos()
                ))
                .toList();
    }
}
