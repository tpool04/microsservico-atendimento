package br.com.tonypool.atendimento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.tonypool.atendimento.model.Atendimento;

@Repository
public interface IAtendimentoRepository extends JpaRepository<Atendimento, Integer> {

    // Busca todos os atendimentos de um cliente específico
    List<Atendimento> findByIdCliente(Integer idCliente);

    // Busca todos os atendimentos de um serviço específico
    List<Atendimento> findByServico_IdServico(Integer idServico);

    // Busca todos os atendimentos de um profissional específico
    List<Atendimento> findByProfissional_IdProfissional(Integer idProfissional);
}