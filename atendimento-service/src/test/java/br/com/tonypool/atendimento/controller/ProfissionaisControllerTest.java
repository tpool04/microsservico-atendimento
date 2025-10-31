package br.com.tonypool.atendimento.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tonypool.atendimento.model.Atendimento;
import br.com.tonypool.atendimento.model.Profissional;
import br.com.tonypool.atendimento.model.Servico;
import br.com.tonypool.atendimento.repository.IProfissionalRepository;
import br.com.tonypool.atendimento.repository.IServicoRepository;

@WebMvcTest(ProfissionaisController.class)
public class ProfissionaisControllerTest {
	
	  @Autowired
	    private MockMvc mockMvc;
	  
	  @MockBean
	  private IProfissionalRepository profissionalRepository;
	  @MockBean
	    private IServicoRepository servicoRepository;
	  
	  
	  @WithMockUser
	    @Test
	    void deveListarTodosOsProfissionaisComSeusServicos() throws Exception {
	        Servico servico = new Servico();
	        servico.setIdServico(2);
	        servico.setNome("Barba");
	        servico.setValor(25.0);
	        
	        Profissional profissional = new Profissional();
	        profissional.setIdProfissional(3);
	        profissional.setNome("Carlos");
	        profissional.setTelefone("99999-9999");
	        profissional.setServicos(List.of(servico));

	        when(profissionalRepository.findAll()).thenReturn(List.of(profissional));

	        mockMvc.perform(get("/api/profissionais/detalhados"))
	        .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idProfissional").value(3))
            .andExpect(jsonPath("$[0].nome").value("Carlos"))
            .andExpect(jsonPath("$[0].servicos[0].nome").value("Barba"));
	    }

}
