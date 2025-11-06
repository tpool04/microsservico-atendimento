package br.com.tonypool.atendimento.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ClienteRequest {
    private String tipoConsulta; // "ID" ou "CPF"
    private String valor;
    private String correlationId;
}

