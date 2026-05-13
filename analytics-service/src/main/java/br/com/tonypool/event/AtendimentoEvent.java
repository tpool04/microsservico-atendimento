package br.com.tonypool.event;

import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoEvent {
    private Integer profissionalId;
    private String profissionalNome;
    
    private Integer clienteId;
    private String clienteNome;

    private Integer servicoId;
    private String servicoTipo;
    
    private Date data;
    
    private String status;
}
