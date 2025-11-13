# ===========================
#  Stage 1: Build the JAR
# ===========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy POM files for dependency resolution (useful if using a parent pom)
COPY pom.xml ./
COPY ../pom.xml ../

# Copy the application source code
COPY src ./src

# Build the application JAR (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ===========================
#  Stage 2: Run the app
# ===========================
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy built JAR from the builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} user_service.jar

# ✅ Expose HTTP and gRPC ports
EXPOSE 8081
EXPOSE 9095

# ✅ Set environment variables for PostgreSQL connection
# These allow you to override DB credentials easily at runtime (Docker Compose / K8s)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/metrocarpool \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=postgres

# Default startup command
ENTRYPOINT ["java", "-jar", "user_service.jar"]