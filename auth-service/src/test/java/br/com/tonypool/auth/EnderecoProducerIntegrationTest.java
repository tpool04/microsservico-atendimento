package br.com.tonypool.auth;

import br.com.tonypool.auth.dto.EnderecoDTO;
import br.com.tonypool.auth.service.EnderecoProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EnderecoProducerIntegrationTest {

    @Autowired
    private EnderecoProducer enderecoProducer;

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Test
    public void deveEnviarEnderecoViaKafkaEConfirmarPersistenciaNoClienteService() throws InterruptedException {
        // 1. Dados de teste
        Integer idCliente = 1;

        EnderecoDTO dto = new EnderecoDTO();
        dto.setLogradouro("Rua Teste");
        dto.setNumero("123");
        dto.setComplemento("Apto 1");
        dto.setBairro("Centro");
        dto.setCidade("Rio de Janeiro");
        dto.setUf("RJ");
        dto.setCep("12345-678");

        // 2. Envia mensagem Kafka
        enderecoProducer.enviarEndereco(idCliente, dto);

        // 3. Aguarda processamento
        Thread.sleep(3000);

        // 4. Monta headers com token JWT
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJhdGVuZGltZW50b3NhcGkiLCJzdWIiOiIzMjE0NTY3ODkwMCIsImlkQ2xpZW50ZSI6MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc2MjM2NDkyOSwiZXhwIjoxNzYyMzcwOTI5fQ.G-Erq3cJU-Srun8ckn-9LDNgZf7dTKF8lMt3hv7uhS8K94xMOwT0Xd46UtpbKv2j23xSKwOrRJgR0CcWxHHt5A");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 5. Consulta o cliente-service via REST com autenticação
        String url = "http://localhost:8081/clientes/" + idCliente + "/enderecos";
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        // 6. Valida resposta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());

        Map endereco = (Map) response.getBody().get(0);
        assertEquals("Rua Teste", endereco.get("logradouro"));
        assertEquals("123", endereco.get("numero"));
    }

}
