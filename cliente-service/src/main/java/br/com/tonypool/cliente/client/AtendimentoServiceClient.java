package br.com.tonypool.cliente.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;
import java.util.Map;
import br.com.tonypool.cliente.responses.AtendimentoGetResponse;

@FeignClient(name = "atendimento-service", url = "${atendimento-service.url}")
public interface AtendimentoServiceClient {
    @PostMapping("/api/atendimentos/por-cliente")
    Map<Integer, List<AtendimentoGetResponse>> buscarAtendimentosPorClientes(
        @RequestBody List<Integer> ids,
        @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/atendimentos/{id}")
    AtendimentoGetResponse buscarAtendimentoPorId(
        @PathVariable("id") Integer id,
        @RequestHeader("Authorization") String authorization
    );
}