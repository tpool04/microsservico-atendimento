package br.com.tonypool.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.tonypool.auth.model.Perfil;

public interface IPerfilRepository extends JpaRepository<Perfil, Integer> {
}
