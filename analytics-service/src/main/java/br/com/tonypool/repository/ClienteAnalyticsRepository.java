package br.com.tonypool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.tonypool.model.ClienteAnalytics;

public interface ClienteAnalyticsRepository extends JpaRepository<ClienteAnalytics, Long> {

    Optional<ClienteAnalytics> findByClienteId(Integer clienteId);
}

