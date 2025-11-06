package br.com.tonypool.cliente.requests;

import br.com.tonypool.cliente.dto.EnderecoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class EnderecoRequest {

    private Integer idCliente;
    private EnderecoDTO endereco;
    private String correlationId;
}
