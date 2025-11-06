package br.com.tonypool.cliente.responses;

import br.com.tonypool.cliente.dto.ClienteDTO;

public class ClienteResponse {

    private ClienteDTO cliente;
    private String correlationId;

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}

