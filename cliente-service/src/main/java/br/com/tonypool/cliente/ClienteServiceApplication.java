package br.com.tonypool.cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.tonypool.cliente")
@EnableFeignClients(basePackages = "br.com.tonypool.cliente.client")
public class ClienteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClienteServiceApplication.class, args);
    }
}
