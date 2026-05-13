package br.com.tonypool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoEventBKP {
    private Integer id;
    private Integer profissionalId;
    private String profissionalNome;
    private Integer clienteId;
    private String clienteNome;

    private Integer servicoId;
    private String servicoTipo;
    
    private Date data;
    private String tipo;
    private String status;
}

