package br.com.tonypool.atendimento.responses;

import java.util.List;

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
public class ServicoGetResponse {

	private Integer idServico;
	private String nome;
	private Double valor;
	private List<ProfissionalGetResponse> profissionais;
}