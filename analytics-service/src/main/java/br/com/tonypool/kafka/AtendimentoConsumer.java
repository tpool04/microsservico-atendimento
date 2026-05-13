package br.com.tonypool.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import br.com.tonypool.event.AtendimentoEvent;
import br.com.tonypool.service.AnalyticsService;

@Service
public class AtendimentoConsumer {

    private final AnalyticsService analyticsService;

    public AtendimentoConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "atendimentos.finalizados", groupId = "analytics-service")
    public void consumirEvento(AtendimentoEvent event) {
        analyticsService.processarEvento(event);
    }
}

