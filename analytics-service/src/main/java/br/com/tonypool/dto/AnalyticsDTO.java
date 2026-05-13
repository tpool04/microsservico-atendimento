package br.com.tonypool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsDTO {
    private Integer profissionalId;
    private String profissionalNome;
    private Integer totalAtendimentos;
}
