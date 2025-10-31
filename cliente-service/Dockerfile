# Etapa de build
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build
COPY . ./
RUN mvn clean package -f pom.xml -pl cliente-service -am -DskipTests -U -Dmaven.wagon.http.retryHandler.count=3

# Etapa de execução
FROM openjdk:17-jdk-slim
WORKDIR /app

# Instala curl para baixar o script
RUN apt-get update && apt-get install -y curl
RUN curl -o /wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x /wait-for-it.sh

# Variáveis de ambiente
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/atendimentosapi
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=coti
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

EXPOSE 8081

COPY --from=build /build/cliente-service/target/*.jar app.jar
ENTRYPOINT ["/wait-for-it.sh", "db:5432", "--", "java", "-jar", "app.jar"]
