package br.com.tonypool.cliente.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import br.com.tonypool.cliente.dto.EnderecoDTO;
import br.com.tonypool.cliente.repository.IEnderecoRepository;

@Service
public class EnderecoService {

    @Autowired
    private IEnderecoRepository enderecoRepository;

    public List<EnderecoDTO> listarEnderecosPorCliente(Integer idCliente) {
        return enderecoRepository.findById(idCliente).stream()
            .map(endereco -> new EnderecoDTO(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep()
            ))
            .collect(Collectors.toList());
    }
}

