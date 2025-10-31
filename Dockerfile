# Etapa de build
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build
COPY . ./
RUN mvn clean package -f pom.xml -pl atendimento-service -am -DskipTests

# Etapa de execução
FROM openjdk:17-jdk-slim
WORKDIR /app

# Instala curl para baixar o script
RUN apt-get update && apt-get install -y curl

# Baixa o wait-for-it.sh e dá permissão de execução
RUN curl -o /wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x /wait-for-it.sh

# Variáveis de ambiente para banco do Render
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d3ft68umcj7s73et2v40-a:5432/attendance_db_lhsz
ENV SPRING_DATASOURCE_USERNAME=attendance_db_lhsz_user
ENV SPRING_DATASOURCE_PASSWORD=wct6r2f1UvYQo9kwVet0tSxlY539FPsv
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

EXPOSE 8083

# Copia o .jar gerado
COPY --from=build /build/atendimento-service/target/*.jar app.jar

# Usa o script para esperar o banco antes de iniciar
ENTRYPOINT ["/wait-for-it.sh", "dpg-d3ft68umcj7s73et2v40-a:5432", "--", "java", "-jar", "app.jar"]
