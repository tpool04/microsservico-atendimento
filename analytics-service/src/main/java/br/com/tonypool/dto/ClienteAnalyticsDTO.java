package br.com.tonypool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAnalyticsDTO {

    private Integer clienteId;
    private String clienteNome;
    private Long totalServicos;
    private Long servicosFinalizados;
}

