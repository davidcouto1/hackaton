# Dockerfile para aplicação Java Spring Boot
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
# Permite selecionar o profile via variável de ambiente SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","app.jar"]
