# Etapa de build
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build
COPY . ./
RUN mvn clean package -f pom.xml -pl atendimento-service -am -DskipTests -U -Dmaven.wagon.http.retryHandler.count=3

# Etapa de execução
FROM openjdk:17-jdk-slim
WORKDIR /app

# Instala curl para baixar o script de espera
RUN apt-get update && apt-get install -y curl \
    && curl -o /wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x /wait-for-it.sh

# Variáveis de ambiente do banco
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/atendimentosapi
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=coti
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Porta da aplicação
EXPOSE 8083

# Copia o .jar gerado
COPY --from=build /build/atendimento-service/target/*.jar app.jar

# Aguarda o banco antes de iniciar a aplicação
ENTRYPOINT ["/wait-for-it.sh", "db:5432", "--", "java", "-jar", "app.jar"]
