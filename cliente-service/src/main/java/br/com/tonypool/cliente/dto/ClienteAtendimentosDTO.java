package br.com.tonypool.cliente.dto;

import br.com.tonypool.cliente.responses.AtendimentoGetResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAtendimentosDTO {
    private ClienteDTO cliente;
    private List<AtendimentoGetResponse> atendimentos;
}
