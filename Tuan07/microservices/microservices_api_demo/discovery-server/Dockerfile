<<<<<<< HEAD
<<<<<<< HEAD
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY discovery-server discovery-server
COPY product-service product-service
COPY inventory-service inventory-service
COPY notification-service notification-service
COPY order-service order-service
COPY api-gateway api-gateway

RUN mvn -pl discovery-server -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar /app/discovery-server.jar
COPY --from=builder /build/discovery-server/src/main/resources/application-docker.properties /app/config/application-docker.properties

ENV SPRING_CONFIG_LOCATION=file:/app/config/application-docker.properties

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/discovery-server.jar"]
=======
=======
>>>>>>> dev
# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the target folder to the container
COPY target/discovery-server-0.0.1-SNAPSHOT.jar /app/discovery-server.jar

# Copy application-docker.properties
COPY src/main/resources/application-docker.properties /app/config/application-docker.properties

# Cấu hình Java để sử dụng application-docker.properties
ENV SPRING_CONFIG_LOCATION=file:/app/config/application-docker.properties

# Expose the port the app runs on
EXPOSE 8761

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/discovery-server.jar"]
<<<<<<< HEAD
>>>>>>> 5097b7c3 (Tuan07/Init commit for Tuan07)
=======
>>>>>>> dev
