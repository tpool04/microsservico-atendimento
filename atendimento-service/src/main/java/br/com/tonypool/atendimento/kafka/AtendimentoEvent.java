package br.com.tonypool.atendimento.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoEvent {
    private Integer id;
    private Integer profissionalId;
    private String profissionalNome;
    private Date data;
    private String tipo;
    private String status;
}