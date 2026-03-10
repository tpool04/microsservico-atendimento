package br.com.tonypool.atendimento.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tonypool.atendimento.model.Profissional;
import br.com.tonypool.atendimento.model.Servico;
import br.com.tonypool.atendimento.repository.IAtendimentoRepository;
import br.com.tonypool.atendimento.repository.IProfissionalRepository;
import br.com.tonypool.atendimento.repository.IServicoRepository;

@WebMvcTest(ServicosController.class)
public class ServicoControllerTest {
	
	 @Autowired
	    private MockMvc mockMvc;
	 
	 @MockBean
	    private IServicoRepository servicoRepository;
	 
	 @MockBean
	    private IProfissionalRepository profissionalService;
	 
	 @MockBean
	    private IAtendimentoRepository atendimentoRepository;
	 
	 @WithMockUser
	    @Test
	    void deveRetornarListaDeServicos() throws Exception {
		 	        Servico servico = new Servico();
	        servico.setIdServico(1);
	        servico.setNome("Corte de Cabelo");
	        servico.setValor(50.0);
	        
	        Profissional profissional = new Profissional();
	        profissional.setIdProfissional(10);
	        profissional.setNome("João");
	        profissional.setTelefone("21999999999");
	        servico.setProfissionais(List.of(profissional));
	        
	        when(servicoRepository.findAll()).thenReturn(List.of(servico));
	        
	        mockMvc.perform(get("/api/servicos"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$[0].idServico").value(1))
	        .andExpect(jsonPath("$[0].nome").value("Corte de Cabelo"))
	        .andExpect(jsonPath("$[0].valor").value(50.0))
	        .andExpect(jsonPath("$[0].profissionais[0].idProfissional").value(10))
	        .andExpect(jsonPath("$[0].profissionais[0].nome").value("João"))
	        .andExpect(jsonPath("$[0].profissionais[0].telefone").value("21999999999"));
		 
	 }

}
