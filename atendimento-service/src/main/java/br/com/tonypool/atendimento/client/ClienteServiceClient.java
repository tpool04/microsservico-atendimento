package br.com.tonypool.atendimento.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.tonypool.atendimento.dto.ClienteDTO;
import br.com.tonypool.atendimento.responses.AtendimentoGetResponse;

//@FeignClient(name = "cliente-service", url = "${cliente-service.url}")
//public interface ClienteServiceClient {
//	
//    @GetMapping("/api/clientes/{id}")
//    ClienteDTO buscarPorId(@PathVariable("id") Integer id);
//    
//    @GetMapping("/api/clientes/cpf/{cpf}")
//    ClienteDTO buscarPorCpf(@PathVariable("cpf") String cpf);
//    
//}

