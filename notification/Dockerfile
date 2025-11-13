# ===========================
#  Stage 1: Build the JAR
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy Maven project files first to leverage build caching
COPY pom.xml ./
# Optional: copy parent pom if using a multi-module structure
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

# Copy JAR from the builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} notification_service.jar

# Expose both HTTP and gRPC ports (as defined in application.yaml)
EXPOSE 8081        # HTTP server port
EXPOSE 9092        # gRPC server port

# Default startup command
ENTRYPOINT ["java", "-jar", "notification_service.jar"]