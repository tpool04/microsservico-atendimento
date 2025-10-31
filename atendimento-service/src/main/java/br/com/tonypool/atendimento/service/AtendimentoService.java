package br.com.tonypool.atendimento.service;

import br.com.tonypool.atendimento.model.Atendimento;
import br.com.tonypool.atendimento.repository.IAtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private IAtendimentoRepository atendimentoRepository;

    public List<Atendimento> buscarPorCliente(Integer id) {
        List<Atendimento> atendimentos = atendimentoRepository.findByIdCliente(id);
        if (atendimentos.isEmpty()) {
            throw new RuntimeException("Nenhum atendimento encontrado para o cliente");
        }
        return atendimentos;
    }
    
    public Atendimento salvar(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }
}

