package br.com.tonypool.cliente.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoGetResponse {
	private Integer idAtendimento;
	private String dataHora;
	private String nomeServico;
	private Double valorServico;
	private String nomeProfissional;
	private String telefoneProfissional;
	private String nomeCliente;
	private String cpfCliente;
	private String observacoes;
	private String status;
}

