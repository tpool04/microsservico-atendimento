package br.com.tonypool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tonypool.dto.ServicoAnalyticsDTO;
import br.com.tonypool.model.ServicoAnalytics;
import br.com.tonypool.event.AtendimentoEvent;
import br.com.tonypool.repository.ServicoAnalyticsRepository;

@Service
public class ServicoAnalyticsService {

    private final ServicoAnalyticsRepository repository;

    public ServicoAnalyticsService(ServicoAnalyticsRepository repository) {
        this.repository = repository;
    }

    public void processar(AtendimentoEvent event) {

        // Busca o serviço ou cria um novo registro
        ServicoAnalytics analytics = repository.findByServicoId(event.getServicoId())
                .orElse(new ServicoAnalytics(
                        event.getServicoId(),
                        event.getServicoTipo(),
                        0L
                ));

        // Incrementa total de execuções do serviço
        analytics.incrementarTotal();

        repository.save(analytics);
    }

    public List<ServicoAnalyticsDTO> listarServicos() {
        return repository.findAll()
                .stream()
                .map(s -> new ServicoAnalyticsDTO(
                        s.getServicoId(),
                        s.getServicoTipo(),
                        s.getTotalExecutado()
                ))
                .toList();
    }
}

