################################### BUILD STAGE ##################################
FROM maven:3.9.9-eclipse-temurin-24-alpine AS builder

WORKDIR /restful-api

COPY . .

RUN mvn clean install -DskipTests -B

################################### RUNTIME STAGE ##################################
FROM openjdk:24-jdk-slim

WORKDIR /restful-api

COPY --from=builder /restful-api/target/*.jar restful-api.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1
  
ENTRYPOINT [ "java", "-jar", "restful-api.jar" ]