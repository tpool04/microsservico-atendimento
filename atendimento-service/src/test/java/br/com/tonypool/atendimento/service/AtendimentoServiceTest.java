package br.com.tonypool.atendimento.service;

import br.com.tonypool.atendimento.model.Atendimento;
import br.com.tonypool.atendimento.model.Servico;
import br.com.tonypool.atendimento.repository.IAtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private IAtendimentoRepository atendimentoRepository;

    @InjectMocks
    private AtendimentoService atendimentoService;

    @Test
    void deveRetornarPrimeiroAtendimentoDoCliente() {
        Atendimento atendimento = new Atendimento();
        Servico servico = new Servico();
        servico.setNome("Servico");
        
        atendimento.setServico(servico);
        when(atendimentoRepository.findByIdCliente(1)).thenReturn(List.of(atendimento));

        List<Atendimento> resultado = atendimentoService.buscarPorCliente(1);

        assertNotNull(resultado);
        assertEquals("Servico", atendimento.getServico().getNome());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoTemAtendimento() {
        when(atendimentoRepository.findByIdCliente(99)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> atendimentoService.buscarPorCliente(99));
    }
    
    @Test
    void deveSalvarAtendimentoComSucesso() {
    	LocalDate localDate = LocalDate.now();
    	Date dataHora = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Atendimento atendimento = new Atendimento();
       
        atendimento.setDataHora(dataHora);

        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimento);

        Atendimento resultado = atendimentoService.salvar(atendimento);

        assertNotNull(resultado);
        assertEquals(atendimento.getDataHora(), resultado.getDataHora());
        verify(atendimentoRepository, times(1)).save(atendimento);
    }

}

