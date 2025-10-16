package br.com.tonypool.atendimento.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "br.com.tonypool.atendimento.client")
public class FeignConfig {
    // Nada mais aqui — só ativa os Feign Clients
}
