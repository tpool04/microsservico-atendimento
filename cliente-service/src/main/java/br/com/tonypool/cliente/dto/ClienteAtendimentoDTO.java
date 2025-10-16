package br.com.tonypool.cliente.dto;

import br.com.tonypool.cliente.responses.AtendimentoGetResponse;

public class ClienteAtendimentoDTO {
    private ClienteDTO cliente;
    private AtendimentoGetResponse atendimento;

    public ClienteAtendimentoDTO() {}

    public ClienteAtendimentoDTO(ClienteDTO cliente, AtendimentoGetResponse atendimento) {
        this.cliente = cliente;
        this.atendimento = atendimento;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public AtendimentoGetResponse getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(AtendimentoGetResponse atendimento) {
        this.atendimento = atendimento;
    }
}
