package br.com.tonypool.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.tonypool.auth.dto.EnderecoDTO;

@FeignClient(name = "cliente-service", url = "${cliente-service.url}")
public interface ClienteServiceClient {
    @PostMapping("/api/clientes/{id}/endereco")
    void vincularEndereco(@PathVariable("id") Long idCliente, @RequestBody EnderecoDTO endereco);
}

