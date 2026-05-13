package br.com.tonypool.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servico_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer servicoId;
    private String servicoTipo;

    private Long totalExecutado = 0L;

    public ServicoAnalytics(Integer servicoId, String servicoTipo, Long totalExecutado) {
        this.servicoId = servicoId;
        this.servicoTipo = servicoTipo;
        this.totalExecutado = totalExecutado;
    }

    public void incrementarTotal() {
        this.totalExecutado++;
    }
}
