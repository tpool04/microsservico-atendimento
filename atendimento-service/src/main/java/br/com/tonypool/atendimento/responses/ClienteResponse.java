package br.com.tonypool.atendimento.responses;

import br.com.tonypool.atendimento.dto.ClienteDTO;
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

public class ClienteResponse {
    private ClienteDTO cliente;
    private String correlationId;
}

