package br.com.tonypool.auth.repository;

import br.com.tonypool.auth.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    boolean existsByNome(String nome);
}
