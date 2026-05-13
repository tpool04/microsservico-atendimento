package br.com.tonypool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.tonypool.dto.ClienteAnalyticsDTO;
import br.com.tonypool.model.ClienteAnalytics;
import br.com.tonypool.event.AtendimentoEvent;
import br.com.tonypool.repository.ClienteAnalyticsRepository;

@Service
public class ClienteAnalyticsService {

    private final ClienteAnalyticsRepository repository;

    public ClienteAnalyticsService(ClienteAnalyticsRepository repository) {
        this.repository = repository;
    }

    public void processar(AtendimentoEvent event) {

        // Busca o cliente ou cria um novo registro
        ClienteAnalytics analytics = repository.findByClienteId(event.getClienteId())
                .orElse(new ClienteAnalytics(
                        event.getClienteId(),
                        event.getClienteNome()
                ));

        // Incrementa total de serviços
        analytics.incrementarTotal();

        if ("FINALIZADO".equalsIgnoreCase(event.getStatus())) {
            analytics.incrementarFinalizados();
        }

        // Salva no banco
        repository.save(analytics);
    }

    public List<ClienteAnalyticsDTO> listarClientes() {
        return repository.findAll()
                .stream()
                .map(c -> new ClienteAnalyticsDTO(
                        c.getClienteId(),
                        c.getClienteNome(),
                        c.getTotalServicos(),
                        c.getServicosFinalizados()
                ))
                .toList();
    }
}

