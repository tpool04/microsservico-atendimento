package br.com.tonypool.atendimento.dto;

import java.util.List;
import br.com.tonypool.atendimento.responses.AtendimentoGetResponse;
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
public class ClienteAtendimentosDTO {
    private ClienteDTO cliente;
    private List<AtendimentoGetResponse> atendimentos;
    private List<AtendimentoGetResponse> status;
}
