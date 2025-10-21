package br.com.tonypool.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.tonypool.auth.client.ClienteServiceClient;
import br.com.tonypool.auth.model.Cliente;
import br.com.tonypool.auth.service.TwoFactorAuthService;
import br.com.tonypool.auth.dto.ClienteDTO;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

	@Autowired
	private ClienteServiceClient clienteServiceClient;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/ativar/{idCliente}")
    public ResponseEntity<?> ativar2FA(@PathVariable Integer idCliente) {
        try {
            //Cliente cliente = clienteServiceClient.buscarPorId(idCliente);
            ClienteDTO cliente = clienteServiceClient.buscarPorId(idCliente);
            if (cliente == null) {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }
            String otpUrl = twoFactorAuthService.setup2FA(cliente);
            return ResponseEntity.ok(java.util.Collections.singletonMap("qrCodeUrl", otpUrl));
        } catch (Exception e) {
            String tipoErro = e.getClass().getSimpleName();
            String mensagem = tipoErro + ": " + e.getMessage();
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("error", mensagem));
        }
    }
}