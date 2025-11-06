package br.com.tonypool.cliente.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Integer idCliente;

    public ClienteNaoEncontradoException(Integer idCliente) {
        super("Cliente não encontrado: " + idCliente);
        this.idCliente = idCliente;
    }

    public Integer getIdCliente() {
        return idCliente;
    }
}
