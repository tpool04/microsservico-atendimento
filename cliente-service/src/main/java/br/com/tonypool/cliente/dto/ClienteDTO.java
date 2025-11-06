package br.com.tonypool.cliente.dto;

import br.com.tonypool.cliente.model.Cliente;
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
    
    public static ClienteDTO fromEntity(Cliente cliente) {
        if (cliente == null) return null;

        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getIdCliente()); // ou cliente.getId() dependendo da sua entidade
        dto.setNome(cliente.getNome());
        dto.setCpf(cliente.getCpf());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setIs2FAEnabled(cliente.getIs2FAEnabled());
        return dto;
    }
}
