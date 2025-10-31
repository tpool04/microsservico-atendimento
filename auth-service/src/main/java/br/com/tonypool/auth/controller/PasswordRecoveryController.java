package br.com.tonypool.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonypool.auth.requests.PasswordForgotRequest;
import br.com.tonypool.auth.requests.PasswordResetRequest;
import br.com.tonypool.auth.service.PasswordRecoveryService;

@RestController
@RequestMapping("/api/auth/password")
@CrossOrigin
public class PasswordRecoveryController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryController.class);

    private final PasswordRecoveryService recoveryService;

    @Autowired
    public PasswordRecoveryController(PasswordRecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }

    @PostMapping("/forgot")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordForgotRequest request) {
        try {
            recoveryService.requestPasswordReset(request.getEmail());
            // Retornar mensagem genérica para evitar enumeração de contas
            return ResponseEntity.ok().body("Se o e-mail existir, você receberá instruções para redefinir a senha.");
        } catch (Exception e) {
            logger.error("Erro no endpoint forgotPassword: {}", e.getMessage(), e);
            // Ainda assim retornar 200 para não revelar detalhes
            return ResponseEntity.ok().body("Se o e-mail existir, você receberá instruções para redefinir a senha.");
        }
    }

    @PostMapping("/reset")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            recoveryService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok().body("Senha alterada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro no endpoint resetPassword: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao alterar a senha.");
        }
    }
}