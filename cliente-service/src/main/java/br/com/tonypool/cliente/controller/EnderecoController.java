package br.com.tonypool.cliente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.tonypool.cliente.dto.EnderecoDTO;
import br.com.tonypool.cliente.service.EnderecoService;

@RestController
@RequestMapping("/clientes")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/{idCliente}/enderecos")
    public ResponseEntity<List<EnderecoDTO>> listarEnderecos(@PathVariable Integer idCliente) {
        List<EnderecoDTO> enderecos = enderecoService.listarEnderecosPorCliente(idCliente);
        return ResponseEntity.ok(enderecos);
    }
}

