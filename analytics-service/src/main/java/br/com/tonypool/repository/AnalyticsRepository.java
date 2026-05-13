package br.com.tonypool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.tonypool.model.Analytics;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Integer> {
	Optional<Analytics> findByProfissionalId(Integer profissionalId);
}
