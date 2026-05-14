# Project System Overview

This document is a fast, self-contained overview of the system so a new agent can get up to speed quickly.

## 1) High-level summary
- The backend is a Spring Boot microservices system with service discovery (Eureka) and API Gateway.
- The frontend is a React app (Vite) that calls the gateway with the `/v1/api` prefix.
- Core infrastructure includes Kafka, MongoDB, Redis, Zipkin, and Prometheus.

## 2) Key services and ports (default)

| Service | Purpose | Port | Notes |
| --- | --- | --- | --- |
| api-gateway | Single entry point for frontend | 8181 | Routes to services, uses `/v1/api` prefix |
| eureka-server | Service discovery | 8761 | All services register here |
| user-service | Auth, users | 8081 | Mongo + Redis |
| product-service | Products | 8082 | Mongo + Redis |
| order-service | Orders | 8083 | Mongo + Redis + Kafka |
| notification-service | Email notifications | 8084 | Kafka consumer + SMTP |
| payment-service | Payment | 8085 | Payment flow |
| mongo | MongoDB | 27017 | Data store |
| redis | Redis | 6379 | Cache/session |
| kafka-1 | Kafka broker | 9092 | External listener |
| kafka-2 | Kafka broker | 9094 | External listener |
| kafka-3 | Kafka broker | 9096 | External listener |
| kafka-ui | Kafka UI | 8080 | Inspect topics |
| zipkin | Tracing | 9411 | Distributed tracing |
| prometheus | Metrics | 9090 | Monitoring |

All services are defined and wired in [ProjectWeb-BE/docker-compose.yml](ProjectWeb-BE/docker-compose.yml).

## 3) API Gateway routing model
- Base API prefix is `/v1/api`.
- Gateway routes to individual services and strips the prefix when needed.
- Product route uses `StripPrefix=2` to match product-service endpoints (see the gateway config in [ProjectWeb-BE/api-gateway](ProjectWeb-BE/api-gateway)).

## 4) Notification flow (Kafka -> Email)
- Producer publishes JSON events to Kafka topic `notificationTopic`.
- `notification-service` consumes events with fields `message` and `userId`.
- It resolves the email by calling user-service, then sends mail via SMTP.
- Mail configuration is in [ProjectWeb-BE/notification-service/src/main/resources/application.properties](ProjectWeb-BE/notification-service/src/main/resources/application.properties).

Recent test confirmed the flow: the service consumed events and logged "Email sent successfully!".

## 5) Frontend
- The React app lives under `ProjectWeb-main/FE/react-e-commerce`.
- It calls the gateway using `VITE_API_URL` (typically `http://localhost:8181/v1/api`).
- Product detail page is implemented in the FE and includes quantity input for add-to-cart.

## 6) Typical run workflow
1) Start infrastructure and services:
   - `docker compose up -d --build` from [ProjectWeb-BE](ProjectWeb-BE).
2) Start the frontend:
   - From `ProjectWeb-main/FE/react-e-commerce`, run `npm install` then `npm run dev`.

## 7) Quick test checklist
- Gateway health: request any known endpoint via `/v1/api`.
- Product list: confirm products load and product detail page opens.
- Auth: register/login and verify token storage.
- Order: checkout and ensure inventory decreases.
- Notification: publish to Kafka `notificationTopic` and check notification-service logs for "Received message" and "Email sent successfully!".

## 8) Known issues and tips
- `notification-service` logs Eureka connection errors if `EUREKA_SERVER_URL` is not set for that service. This does not block Kafka consumption or email sending.
- Mailtrap inbox must match the SMTP credentials used by the notification service.

## 9) Helpful docs
- Entry index: [ProjectWeb-BE/DOCUMENTATION_INDEX.md](ProjectWeb-BE/DOCUMENTATION_INDEX.md)
- Quick start: [ProjectWeb-BE/QUICK_START.md](ProjectWeb-BE/QUICK_START.md)
- Architecture: [ProjectWeb-BE/SOLUTION_ARCHITECTURE.md](ProjectWeb-BE/SOLUTION_ARCHITECTURE.md)
- Final checklist: [ProjectWeb-BE/FINAL_VERIFICATION_CHECKLIST.md](ProjectWeb-BE/FINAL_VERIFICATION_CHECKLIST.md)
