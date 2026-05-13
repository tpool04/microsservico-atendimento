package br.com.tonypool.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente_servico_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteServicoAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer clienteId;
    private String clienteNome;

    private Integer servicoId;
    private String servicoTipo;
    
    private Date dataAtendimento;

    private Long totalExecutado = 0L;

    public ClienteServicoAnalytics(Integer clienteId, String clienteNome,
                                   Integer servicoId, String servicoTipo,
                                   Date dataAtendimento) {
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.servicoId = servicoId;
        this.servicoTipo = servicoTipo;
        this.dataAtendimento = dataAtendimento;
        this.totalExecutado = 0L;
    }

    public void incrementar() {
        this.totalExecutado++;
    }
}

