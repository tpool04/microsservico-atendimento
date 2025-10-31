package br.com.tonypool.auth.service;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private final JavaMailSender mailSender;

    // Prefer spring.mail.from, fallback to spring.mail.username, then to default
    @Value("${spring.mail.from:${spring.mail.username:no-reply@localhost}}")
    private String mailFrom;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

	@Autowired
	private TemplateEngine templateEngine;

	public void sendPasswordResetEmail(String to, String link) {
	    try {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

	        Context context = new Context();
	        context.setVariable("link", link);
	        String htmlContent = templateEngine.process("password-reset", context);

	        helper.setTo(to);
	        helper.setFrom(mailFrom);
	        helper.setSubject("Redefinição de senha");
	        helper.setText(htmlContent, true);

	        mailSender.send(mimeMessage);
	        logger.info("E-mail HTML de recuperação enviado para {}", to);
	    } catch (Exception e) {
	        logger.error("Falha ao enviar e-mail HTML para {}: {}", to, e.getMessage(), e);
	    }
	}

    public void sendPasswordChangedNotification(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(mailFrom);
            message.setSubject("Senha alterada");
            message.setText("Sua senha foi alterada com sucesso. Se não foi você, contate o suporte imediatamente.");
            mailSender.send(message);
            logger.info("Notificação de alteração de senha enviada para {}", to);
        } catch (Exception e) {
            logger.error("Falha ao enviar notificação de alteração de senha para {}: {}", to, e.getMessage(), e);
        }
    }
}