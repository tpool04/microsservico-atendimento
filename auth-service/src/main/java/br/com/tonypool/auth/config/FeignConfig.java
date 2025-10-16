package br.com.tonypool.auth.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "br.com.tonypool.auth.client")
public class FeignConfig {
    // Nada mais aqui — só ativa os Feign Clients
}
