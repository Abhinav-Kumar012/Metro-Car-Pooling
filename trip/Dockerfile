# ===========================
#  Stage 1: Build the JAR
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom files for dependency resolution (useful for multi-module builds)
COPY pom.xml ./
COPY ../pom.xml ../

# Copy source code
COPY src ./src

# Build the JAR (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ===========================
#  Stage 2: Run the app
# ===========================
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} trip_service.jar

# ✅ Expose both HTTP and gRPC ports
EXPOSE 8081
EXPOSE 9094

# Default command to run the service
ENTRYPOINT ["java", "-jar", "trip_service.jar"]