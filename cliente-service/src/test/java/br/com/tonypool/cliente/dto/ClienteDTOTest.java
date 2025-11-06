package br.com.tonypool.cliente.dto;

import br.com.tonypool.cliente.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDTOTest {

    @Test
    void deveConverterClienteParaDTOCorretamente() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Tonypool");
        cliente.setCpf("12345678900");
        cliente.setEmail("tony@example.com");
        cliente.setTelefone("21999999999");
        cliente.setIs2FAEnabled(true);

        // Act
        ClienteDTO dto = ClienteDTO.fromEntity(cliente);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Tonypool", dto.getNome());
        assertEquals("12345678900", dto.getCpf());
        assertEquals("tony@example.com", dto.getEmail());
        assertEquals("21999999999", dto.getTelefone());
        assertTrue(dto.getIs2FAEnabled());
    }

    @Test
    void deveRetornarNullSeClienteForNull() {
        // Act
        ClienteDTO dto = ClienteDTO.fromEntity(null);

        // Assert
        assertNull(dto);
    }
}

