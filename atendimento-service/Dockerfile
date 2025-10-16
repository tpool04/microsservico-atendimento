# Etapa de build
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build
COPY . ./
RUN mvn clean package -f pom.xml -pl atendimento-service -am -DskipTests -U -Dmaven.wagon.http.retryHandler.count=3

# Etapa de execução
FROM openjdk:17-jdk-slim
WORKDIR /app

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/atendimentosapi
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=coti
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
EXPOSE 8083
EXPOSE 5005

COPY --from=build /build/atendimento-service/target/atendimentos-service-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
