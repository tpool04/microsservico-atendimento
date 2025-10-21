package br.com.tonypool.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import br.com.tonypool.auth.dto.ClienteDTO;
import br.com.tonypool.auth.model.Cliente;
import br.com.tonypool.auth.repository.IClienteRepository;
import br.com.tonypool.auth.repository.IPerfilRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TwoFactorAuthService {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthService.class);

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IPerfilRepository perfilRepository;

    private final GoogleAuthenticator gAuth;

    public TwoFactorAuthService() {
        gAuth = new GoogleAuthenticator();
        gAuth.setCredentialRepository(new SimpleCredentialRepository());
    }

    public String setup2FA(ClienteDTO clienteDTO) {
        try {
            GoogleAuthenticatorKey key = gAuth.createCredentials();
            String secret = key.getKey();
            logger.info("[2FA] Gerando secret para cliente {}: {}", clienteDTO.getId(), secret);
            Cliente cliente = clienteRepository.findById(clienteDTO.getId()).orElseThrow(() -> new RuntimeException("Cliente não encontrado no banco."));

                cliente.setSecret2FA(secret);
                cliente.setIs2FAEnabled(true);

                if (cliente.getPerfil() == null) {
                    cliente.setPerfil(perfilRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Perfil USER (id=2) não encontrado.")));
                }
            clienteRepository.save(cliente);
            logger.info("[2FA] Secret salvo para cliente {}: {}", cliente.getIdCliente(), cliente.getSecret2FA());
            String otpUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("TonypoolApp", cliente.getEmail(), key);
            return otpUrl;
        } catch (Exception e) {
            logger.error("[2FA] Erro ao ativar 2FA para cliente {}: {}", clienteDTO.getId(), e.getMessage());
            throw new RuntimeException("Erro ao ativar 2FA: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    public boolean verifyCode(ClienteDTO clienteDTO, int code) {
        String secret = clienteDTO.getSecret2FA();
        logger.debug("[2FA] Secret recebido para validação: '{}'", secret);
        logger.debug("[2FA] Código recebido para validação: '{}'", code);

        if (secret == null || secret.isEmpty()) {
            logger.error("[2FA] Erro: Secret não pode ser nulo ou vazio para o cliente {}", clienteDTO.getId());
            return false;
        }

        if (!secret.matches("^[A-Z2-7]+=*$")) {
            logger.error("[2FA] Erro: Secret em formato inválido para o cliente {}: {}", clienteDTO.getId(), secret);
            return false;
        }

        if (String.valueOf(code).length() != 6) {
            logger.error("[2FA] Erro: Código informado não possui 6 dígitos para o cliente {}: {}", clienteDTO.getId(), code);
            return false;
        }

        try {
            int expectedCode = gAuth.getTotpPassword(secret);
            logger.info("[2FA] Código TOTP esperado pelo backend para secret {}: {} (horário atual)", secret, expectedCode);
            boolean result = gAuth.authorize(secret, code);
            logger.info("[2FA] Verificando código recebido: {} para secret: {} | Resultado: {}", code, secret, result);
            return result;
        } catch (Exception e) {
            logger.error("[2FA] Exceção ao validar código: {} para cliente {} - {}: {}", code, clienteDTO.getId(), e.getClass().getSimpleName(), e.getMessage(), e);
            logger.error("[2FA] Stacktrace:", e);
            return false;
        }
    }

}