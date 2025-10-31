package br.com.tonypool.cliente.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tonypool.cliente.client.AtendimentoServiceClient;
import br.com.tonypool.cliente.model.Cliente;
import br.com.tonypool.cliente.repository.IClienteRepository;
import br.com.tonypool.cliente.repository.IEnderecoRepository;
import br.com.tonypool.cliente.responses.AtendimentoGetResponse;
import br.com.tonypool.cliente.service.ClienteService;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {
	
	 @Autowired
	    private MockMvc mockMvc;
	 
	 @MockBean
	    private IClienteRepository clienteRepository;
	 
	 @MockBean
	    private IEnderecoRepository enderecoRepository;
	    
	 @MockBean
	    private ClienteService clienteService;
	    
	 @MockBean
	    private AtendimentoServiceClient atendimentoServiceClient;
	 
	 @WithMockUser(roles = "ADMIN")
	    @Test
	    void deveListarTodosOsClienteComAtendimentoComProfissionais() throws Exception {
		 
		 	Cliente cliente = new Cliente();
	        cliente.setIdCliente(1);
	        cliente.setNome("Maria");
	        cliente.setCpf("12345678900");
	        cliente.setEmail("maria@email.com");
	        cliente.setTelefone("99999-9999");
	        cliente.setIs2FAEnabled(true);
	        
	        AtendimentoGetResponse atendimento = new AtendimentoGetResponse();
	        atendimento.setIdAtendimento(10);
	        atendimento.setNomeServico("Corte");
	        atendimento.setValorServico(50.0);
	        atendimento.setNomeProfissional("João");
	        atendimento.setTelefoneProfissional("88888-8888");
	        atendimento.setObservacoes("Pontual");
	        
	        when(clienteService.listarTodos()).thenReturn(List.of(cliente));
	        when(atendimentoServiceClient.buscarAtendimentosPorClientes(List.of(1), "Bearer token"))
            .thenReturn(Map.of(1, List.of(atendimento)));
	        
	        mockMvc.perform(get("/api/clientes/atendimentos-clientes")
	                .header("Authorization", "Bearer token"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$[0].cliente.nome").value("Maria"))
	            .andExpect(jsonPath("$[0].cliente.cpf").value("12345678900"))
	            .andExpect(jsonPath("$[0].atendimentos[0].nomeServico").value("Corte"))
	            .andExpect(jsonPath("$[0].atendimentos[0].nomeProfissional").value("João"));
		 
	 }

}
