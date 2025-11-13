# ===========================
#  Stage 1: Build the JAR
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy the Maven build files first (for dependency caching)
COPY pom.xml ./
# Optional: copy parent pom if using a multi-module project
COPY ../pom.xml ../

# Copy the application source code
COPY src ./src

# Build the Spring Boot JAR (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===========================
#  Stage 2: Run the JAR
# ===========================
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} matching_service.jar

# Expose HTTP and gRPC ports based on application.yaml
EXPOSE 8081        # HTTP server port
EXPOSE 9091        # gRPC server port

# Set default command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "matching_service.jar"]