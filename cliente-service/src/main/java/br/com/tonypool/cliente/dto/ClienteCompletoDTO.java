package br.com.tonypool.cliente.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCompletoDTO {
    private ClienteDTO cliente;
    private EnderecoDTO endereco;
}