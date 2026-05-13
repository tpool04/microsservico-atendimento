package br.com.tonypool.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {

    @Id
    private Integer profissionalId;
    private String profissionalNome;
    private Integer totalAtendimentos;

    public void incrementarAtendimentos() {
        this.totalAtendimentos++;
    }
}
