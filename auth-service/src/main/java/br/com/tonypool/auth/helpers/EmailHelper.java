package br.com.tonypool.auth.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailHelper {
    private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    public static void sendPasswordResetEmail(String to, String link) {
        // Implementação simples: logar o envio. Substituir por JavaMailSender em produção.
        logger.info("[EmailHelper] Enviando email de password reset para {} com link: {}", to, link);
    }

    public static void sendPasswordChangedNotification(String to) {
        logger.info("[EmailHelper] Notificando alteração de senha para {}", to);
    }
}
