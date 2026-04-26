FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY discovery-server discovery-server
COPY product-service product-service
COPY inventory-service inventory-service
COPY notification-service notification-service
COPY order-service order-service
COPY api-gateway api-gateway

RUN mvn -pl notification-service -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8084

CMD ["java", "-jar", "app.jar"]
