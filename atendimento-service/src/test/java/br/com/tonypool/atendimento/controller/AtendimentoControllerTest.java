package br.com.tonypool.atendimento.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.tonypool.atendimento.cache.ClienteCache;

import br.com.tonypool.atendimento.dto.ClienteDTO;
import br.com.tonypool.atendimento.model.Atendimento;
import br.com.tonypool.atendimento.model.Profissional;
import br.com.tonypool.atendimento.model.Servico;
import br.com.tonypool.atendimento.repository.IAtendimentoRepository;
import br.com.tonypool.atendimento.repository.IProfissionalRepository;
import br.com.tonypool.atendimento.repository.IServicoRepository;
import br.com.tonypool.atendimento.service.AtendimentoService;
import br.com.tonypool.atendimento.service.ClienteProducer;

@WebMvcTest(AtendimentosController.class)
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtendimentoService atendimentoService;
    
    @MockBean
    private IServicoRepository servicoRepository;
    
    @MockBean
    private IAtendimentoRepository atendimentoRepository;
    
    @MockBean
    private ClienteProducer clienteProducer;

    @MockBean
    private ClienteCache clienteCache;

    
    @MockBean
    private IProfissionalRepository profissionalService;

    @WithMockUser
    @Test
    void deveRetornarListaDeAtendimentosComDadosDoCliente() throws Exception {
        Atendimento atendimento = new Atendimento();
        atendimento.setIdAtendimento(1);
        atendimento.setDataHora(new Date());
        atendimento.setServico(new Servico("Corte", 50.0));
        atendimento.setProfissional(new Profissional("João", "21999999999"));

        when(atendimentoRepository.findByIdCliente(1)).thenReturn(List.of(atendimento));

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Tony");
        clienteDTO.setCpf("12345678900");

        when(clienteCache.buscar(anyString())).thenReturn(clienteDTO);

        mockMvc.perform(get("/api/atendimentos/por-cliente/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].idAtendimento").value(1))
            .andExpect(jsonPath("$[0].nomeCliente").value("Tony"))
            .andExpect(jsonPath("$[0].cpfCliente").value("12345678900"));
    }

}