package br.com.tonypool.atendimento.responses;

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