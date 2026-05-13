package br.com.tonypool.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer clienteId;
    private String clienteNome;

    private Long totalServicos = 0L;
    private Long servicosAbertos = 0L;
    private Long servicosFinalizados = 0L;

    public ClienteAnalytics(Integer clienteId, String clienteNome) {
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.totalServicos = 0L;
        this.servicosAbertos = 0L;
        this.servicosFinalizados = 0L;
    }

    public void incrementarTotal() {
        this.totalServicos++;
    }

    public void incrementarAbertos() {
        this.servicosAbertos++;
    }

    public void incrementarFinalizados() {
        this.servicosFinalizados++;
    }
}

