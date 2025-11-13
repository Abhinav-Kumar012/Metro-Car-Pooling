# ===========================
#  Stage 1: Build the JAR
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy the parent pom.xml (for multi-module projects) if applicable
COPY ../pom.xml ../

# Copy only this module’s pom.xml first (for dependency caching)
COPY pom.xml .

# Pre-download dependencies to speed up rebuilds
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===========================
#  Stage 2: Run the app
# ===========================
FROM eclipse-temurin:21-jre-jammy

# Set working directory inside runtime container
WORKDIR /app

# Copy the built JAR from the builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} driver_service.jar

# Environment variables (optional, makes configuration explicit)
ENV SPRING_PROFILES_ACTIVE=default
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://registry:8761/eureka
ENV SPRING_APPLICATION_NAME=driver

# Expose gRPC port (9090) and optional HTTP port (if you ever add actuators or REST endpoints)
EXPOSE 9090

# Default command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "driver_service.jar"]