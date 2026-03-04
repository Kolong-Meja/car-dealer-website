# ===================== BUILD =====================
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Install required tools
RUN apk add --no-cache bash

# Copy Maven wrapper & config first (better cache)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies only
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw -B clean package -DskipTests

# ===================== RUNTIME =====================
FROM eclipse-temurin:25-jre-alpine AS runner

WORKDIR /app

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

# Create non-root user
RUN addgroup -S faisalspring && adduser -S faisalspring -G faisalspring

# Copy built JAR
COPY --from=builder /app/target/*.jar app.jar

# Fix permissions
RUN chown faisalspring:faisalspring app.jar

USER faisalspring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]