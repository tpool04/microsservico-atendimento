package br.com.tonypool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tonypool.event.AtendimentoEvent;
import br.com.tonypool.model.ClienteServicoAnalytics;
import br.com.tonypool.repository.ClienteServicoAnalyticsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServicoAnalyticsService {

    private final ClienteServicoAnalyticsRepository repository;

    public void processar(AtendimentoEvent event) {

       // var analytics = repository.findByClienteIdAndServicoId(
    	ClienteServicoAnalytics analytics = new ClienteServicoAnalytics(
                event.getClienteId(),
                event.getClienteNome(),
                event.getServicoId(),
                event.getServicoTipo(),
                event.getData()
        );

        //analytics.incrementar();
        //analytics.setDataAtendimento(event.getData());
        analytics.setTotalExecutado(1L);
        repository.save(analytics);
    }

    public List<ClienteServicoAnalytics> listarPorCliente(Integer clienteId) {
        return repository.findByClienteId(clienteId);
    }
    
    public List<ClienteServicoAnalytics> listarTodos() {
        return repository.findAll();
    }
}
