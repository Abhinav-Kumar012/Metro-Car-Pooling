# ===========================
#  Stage 1: Build the JAR
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy the Maven build files first (for dependency caching)
COPY pom.xml ./
# Optional: copy parent POM if using a multi-module setup
COPY ../pom.xml ../

# Copy the source code
COPY src ./src

# Build the Spring Boot JAR (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===========================
#  Stage 2: Run the JAR
# ===========================
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy built JAR from the builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} gateway_service.jar

# Expose the HTTP server port (from application.yaml → 8081)
EXPOSE 8081

# Optional: expose Eureka, gRPC, or management ports if needed
# EXPOSE 8761
# EXPOSE 9090

# Set default command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "gateway_service.jar"]