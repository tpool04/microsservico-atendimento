package br.com.tonypool.auth.requests;

import br.com.tonypool.auth.dto.EnderecoDTO;
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

