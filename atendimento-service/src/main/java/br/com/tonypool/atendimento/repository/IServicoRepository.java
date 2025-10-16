package br.com.tonypool.atendimento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import br.com.tonypool.atendimento.model.Servico;

public interface IServicoRepository extends CrudRepository<Servico, Integer> {

	@Query("select s from Servico s order by s.idServico")
	public List<Servico> findAll();
	
	@Query("select distinct s from Servico s join fetch s.profissionais")
    List<Servico> findAllWithProfissionais();
	
}