package br.com.tonypool.cliente.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.model.Endereco;

public interface IEnderecoRepository extends CrudRepository<Endereco, Integer>{
	@Query("SELECT e FROM Endereco e WHERE e.cliente = :cliente")
    Endereco findByCliente(@Param("cliente") Cliente cliente);

}
