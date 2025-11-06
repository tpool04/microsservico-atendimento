package br.com.tonypool.cliente.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.tonypool.cliente.dto.ClienteDTO;

//@FeignClient(name = "cliente-service", url = "${cliente-service.url}")
//public interface ClienteServiceClient {
//    @GetMapping("/api/clientes/{id}")
//    ClienteDTO buscarPorId(@PathVariable("id") Integer id);
//}

