package br.com.tonypool.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.tonypool.auth.helpers.MD5Helper;
import br.com.tonypool.auth.model.PasswordResetToken;
import br.com.tonypool.auth.model.Cliente;
import br.com.tonypool.auth.repository.IClienteRepository;
import br.com.tonypool.auth.repository.PasswordResetTokenRepository;

@Service
@Transactional
public class PasswordRecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryService.class);

    private final IClienteRepository clienteRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${app.password.reset.token-expiration-minutes:60}")
    private long tokenExpirationMinutes;

    @Value("${app.frontend.reset-url:http://localhost:4200/reset-password}")
    private String frontendResetUrl;

    public PasswordRecoveryService(IClienteRepository clienteRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void requestPasswordReset(String email) {
        try {
            Cliente cliente = clienteRepository.findByEmail(email);
            if (cliente == null) {
                // Não revelar existência do e-mail
                logger.info("Password reset solicitado para email inexistente: {}", email);
                return;
            }

            // Gerar token seguro
            String token = generateSecureToken();
            String tokenHash = sha256Hex(token);

            PasswordResetToken prt = new PasswordResetToken();
            prt.setCliente(cliente);
            prt.setTokenHash(tokenHash);
            prt.setCreatedAt(LocalDateTime.now());
            prt.setExpiryAt(LocalDateTime.now().plusMinutes(tokenExpirationMinutes));
            prt.setUsed(false);

            tokenRepository.save(prt);

            String link = frontendResetUrl + "?token=" + token;

            // Enviar email assincronamente usando EmailService
            new Thread(() -> emailService.sendPasswordResetEmail(cliente.getEmail(), link)).start();
            logger.info("Token de recuperação gerado para cliente id={} e enviado para o email {}", cliente.getIdCliente(), cliente.getEmail());
        } catch (Exception e) {
            logger.error("Erro ao processar password reset para email {}: {}", email, e.getMessage(), e);
        }
    }

    public void resetPassword(String token, String newPassword) {
        try {
            String tokenHash = sha256Hex(token);
            Optional<PasswordResetToken> opt = tokenRepository.findByTokenHashAndUsedFalse(tokenHash);
            if (!opt.isPresent()) {
                throw new IllegalArgumentException("Token inválido ou expirado.");
            }

            PasswordResetToken prt = opt.get();
            if (prt.getExpiryAt() != null && prt.getExpiryAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Token expirado.");
            }

            Cliente cliente = prt.getCliente();
            if (cliente == null) {
                throw new IllegalStateException("Cliente vinculado ao token não encontrado.");
            }

            // Atualizar senha (usar MD5Helper para compatibilidade com sistema atual)
            cliente.setSenha(MD5Helper.encrypt(newPassword));

			// Desabilitar 2FA
            cliente.setSecret2FA(null);
            cliente.setIs2FAEnabled(false);

            clienteRepository.save(cliente);

            prt.setUsed(true);
            tokenRepository.save(prt);

            // Notificar por email usando EmailService
            new Thread(() -> emailService.sendPasswordChangedNotification(cliente.getEmail())).start();

            logger.info("Senha alterada com sucesso para cliente id={}", cliente.getIdCliente());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao resetar senha: {}", e.getMessage(), e);
            throw new RuntimeException("Erro interno ao resetar senha.");
        }
    }

    private String generateSecureToken() throws Exception {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}