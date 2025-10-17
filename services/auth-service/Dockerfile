# Stage 1 — Build the JAR file
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code and build the project
COPY src ./src
RUN mvn clean package -DskipTests


# Stage 2 — Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=builder /app/target/auth-service-*.jar app.jar

EXPOSE 8082

# Environment variables (optional)
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
