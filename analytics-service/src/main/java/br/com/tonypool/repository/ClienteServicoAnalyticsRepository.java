package br.com.tonypool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.tonypool.model.ClienteServicoAnalytics;

public interface ClienteServicoAnalyticsRepository
        extends JpaRepository<ClienteServicoAnalytics, Long> {

    List<ClienteServicoAnalytics> findByClienteId(Integer clienteId);

    Optional<ClienteServicoAnalytics> findByClienteIdAndServicoId(
            Integer clienteId, Integer servicoId);
}

