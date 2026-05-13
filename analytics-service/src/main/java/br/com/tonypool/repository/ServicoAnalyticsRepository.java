package br.com.tonypool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.tonypool.model.ServicoAnalytics;

public interface ServicoAnalyticsRepository extends JpaRepository<ServicoAnalytics, Long> {

    Optional<ServicoAnalytics> findByServicoId(Integer servicoId);
}

