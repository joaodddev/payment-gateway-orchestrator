# Payment Gateway Orchestrator

A fintech-grade payment orchestration API built with **Kotlin + Spring Boot**, featuring event-driven architecture, idempotency guarantees, and production-ready resilience patterns.

## Tech Stack

- **Kotlin** + **Java 21**
- **Spring Boot 3.3**
- **Clean Architecture** + **Domain-Driven Design (DDD)**
- **Apache Kafka** (event-driven payment processing)
- **Redis** (idempotency key storage)
- **PostgreSQL 16** + **Flyway** (migrations)
- **Resilience4j** (Retry + Circuit Breaker)
- **Spring Security** + **JWT**
- **SpringDoc OpenAPI** (Swagger UI)
- **JUnit 5** + **MockK** + **Testcontainers**

## Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register a new user |
| POST | `/api/v1/auth/login` | Authenticate and get JWT token |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/payments` | Create payment (idempotent) |
| GET | `/api/v1/payments` | List all payments |
| GET | `/api/v1/payments/{id}` | Find payment by ID |
| GET | `/api/v1/payments/{id}/status` | Get payment status |
| GET | `/api/v1/payments/payer/{payerId}` | List payments by payer |
| GET | `/api/v1/payments/status/{status}` | List payments by status |

## 👨‍💻 Author

**João Victor**

[![GitHub](https://img.shields.io/badge/GitHub-joaodddev-181717?style=flat&logo=github)](https://github.com/joaodddev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-joaodddev-0A66C2?style=flat&logo=linkedin)](https://linkedin.com/in/joaodddev)
