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

RUN mvn -pl inventory-service -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar /app/inventory-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/inventory-service.jar"]
=======
=======
>>>>>>> dev
# Chọn image base (sử dụng JDK image)
FROM openjdk:21-jdk-slim

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file jar vào container
COPY target/inventory-service-0.0.1-SNAPSHOT.jar /app/inventory-service.jar

# Chạy ứng dụng khi container bắt đầu
ENTRYPOINT ["java", "-jar", "/app/inventory-service.jar"]

# Cấu hình cổng mà ứng dụng sẽ lắng nghe
<<<<<<< HEAD
EXPOSE 8083
>>>>>>> 5097b7c3 (Tuan07/Init commit for Tuan07)
=======
EXPOSE 8083
>>>>>>> dev
