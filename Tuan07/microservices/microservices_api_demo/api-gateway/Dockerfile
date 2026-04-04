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

RUN mvn -pl api-gateway -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
=======
# Chọn image base (sử dụng JDK image)
FROM openjdk:21-jdk-slim

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file jar vào container
COPY target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

# Chạy ứng dụng khi container bắt đầu
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]

# Cấu hình cổng mà ứng dụng sẽ lắng nghe
EXPOSE 8181
>>>>>>> 5097b7c3 (Tuan07/Init commit for Tuan07)
