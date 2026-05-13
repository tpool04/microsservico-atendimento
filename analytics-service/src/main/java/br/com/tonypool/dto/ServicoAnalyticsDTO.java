package br.com.tonypool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoAnalyticsDTO {

    private Integer servicoId;
    private String servicoTipo;
    private Long totalExecutado;
}

