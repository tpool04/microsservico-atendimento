# Microsserviço de Atendimento

Este projeto segue uma arquitetura baseada em microsserviços, com comunicação via Feign Client. Cada serviço é responsável por um domínio específico:
- cliente-service: gerencia dados dos clientes
- atendimento-service: gerencia os atendimentos realizados
- profissional-service: gerencia os profissionais envolvidos
- agendamento-service (opcional): controla horários e disponibilidade

---

## Funcionalidades

- Consultar atendimentos por lista de IDs de clientes
- Retornar dados do atendimento como serviço, profissional, data e observações
- Integração com o `cliente-service` via Feign Client

---

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Feign Client
- Lombok
- PostgreSQL
- Swagger (OpenAPI)
- Maven
- Docker

---

## Endpoint Principal

### `POST /api/atendimentos/por-cliente`

Consulta atendimentos por uma lista de IDs de clientes.

#### Requisição


## Funcionalidades principais

- Criar conta de cliente  
- Acessar conta com autenticação JWT  
- Ativar/verificar 2FA  
- Cadastrar, consultar, reagendar e cancelar atendimentos  
- Gerenciar profissionais e serviços  
- Consultar perfil e status de 2FA

---

## Estrutura de pacotes

br.com.tonypool
├── controllers
├── dto
├── entities
├── repositories
├── requests
├── responses
├── security
├── services
├── config

---

## Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/api/clientes` | Criar conta de cliente |
| POST   | `/api/login` | Autenticar e gerar JWT |
| POST   | `/api/2fa/confirmar` | Confirmar código de verificação |
| POST   | `/api/atendimentos` | Cadastrar atendimento |
| PUT    | `/api/atendimentos/{id}/reagendar` | Reagendar atendimento |
| DELETE | `/api/atendimentos/{id}` | Cancelar atendimento |

---

## Testes

- Testes unitários com JUnit  
- Cobertura para controllers e serviços  
- Exemplos: `ClienteConsultaControllerTest`, `TwoFactorControllerTest`

---

## Documentação

- Swagger disponível em:  
  http://localhost:8080/swagger-ui/index.html

---


## Autor

**Tony Pool**  
Desenvolvedor Backend | Java & Spring Boot  
GitHub: [@tpool04](https://github.com/tpool04)
