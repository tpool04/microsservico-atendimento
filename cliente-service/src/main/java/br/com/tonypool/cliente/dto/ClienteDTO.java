package br.com.tonypool.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
	private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Boolean is2FAEnabled;
}
