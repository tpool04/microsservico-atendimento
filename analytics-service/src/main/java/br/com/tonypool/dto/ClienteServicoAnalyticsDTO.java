package br.com.tonypool.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteServicoAnalyticsDTO {

    private Integer clienteId;
    private String clienteNome;

    private Integer servicoId;
    private String servicoTipo;
    private Date dataAtendimento;

    private Long totalExecutado;
}

