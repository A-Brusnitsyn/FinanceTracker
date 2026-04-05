# 💰 FinanceTracker

[![CI](https://github.com/A-Brusnitsyn/FinanceTracker/actions/workflows/ci.yml/badge.svg)](https://github.com/A-Brusnitsyn/FinanceTracker/actions/workflows/ci.yml)
[![Docker](https://ghcr-badge.egpl.dev/a-brusnitsyn/financetracker/size?color=%2344cc11&label=image+size)](https://github.com/A-Brusnitsyn/FinanceTracker/pkgs/container/financetracker)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, production-ready REST API for tracking personal finances. Built with **Spring Boot 3**, **JWT security**, and **automated CI/CD**. Supports multi-account management, category-based transactions, and real-time balance calculations.

---

## ✨ Features

- 📊 **Income & Expense Tracking** – Create transactions with categories, amounts, and descriptions
- 💳 **Multi-Account Support** – Manage several accounts with independent balances
- 🔐 **JWT Authentication** – Stateless, secure login with role-based access (`USER`/`ADMIN`)
- 🛡 **Input Validation & Custom Exceptions** – Clean error handling and strict DTO validation
- 📖 **Auto-Generated API Docs** – Swagger/OpenAPI UI out of the box
- 🐳 **Docker & Docker Compose** – One-command local deployment
- 🔄 **CI/CD Pipeline** – Automated tests, code style checks, and Docker image publishing to GHCR

---

## 🛠 Tech Stack

| Layer | Technologies |
|-------|--------------|
| **Runtime** | Java 17, Spring Boot 3.1, Spring Security, Spring Data JPA |
| **Database** | PostgreSQL 15, Flyway (migrations), HikariCP |
| **Build & DevOps** | Maven, Docker, GitHub Actions, GitHub Container Registry |
| **Testing** | JUnit 5, Mockito, AssertJ, H2 (in-memory for tests) |
| **Utilities** | Lombok, MapStruct, SpringDoc OpenAPI, SLF4J |

---

## 🚀 Quick Start

### 📦 Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optional, for containerized run)

### 🖥️ Run Locally
```bash
git clone https://github.com/A-Brusnitsyn/FinanceTracker.git
cd FinanceTracker

# Create .env file (see variables below)
cp .env.example .env

# Run with Maven
mvn spring-boot:run
```

### 🐳 Run with Docker
```bash
# Build & start everything (app + PostgreSQL)
docker-compose up -d

# Check logs
docker-compose logs -f app
```

---

## 🔑 Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC connection string | `jdbc:postgresql://localhost:5432/financetracker` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `your_secret_password` |
| `JWT_SECRET` | Secret key for token signing | `a-very-long-random-string-here` |
| `SPRING_PROFILES_ACTIVE` | Active profile | `prod` or `dev` |

---

## 📖 API Documentation

Once the app is running, open Swagger UI:
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Endpoints are grouped by modules: `Auth`, `Accounts`, `Categories`, `Transactions`.

---

## 🔄 CI/CD Pipeline

The project uses **GitHub Actions** for full automation:

| Stage | Description |
|-------|-------------|
| 🔍 **Code Quality** | `spotless:check` enforces consistent formatting |
| 🧪 **Tests** | JUnit 5 + Mockito runs with H2 in-memory DB |
| 🐳 **Docker Build** | Multi-stage build optimized for production |
| 📦 **Publish** | Image pushed to `ghcr.io/a-brusnitsyn/financetracker:latest` |

📊 View pipeline status: [Actions Tab](https://github.com/A-Brusnitsyn/FinanceTracker/actions)

---

## 🐳 Docker & GHCR

Official image is available in **GitHub Container Registry**:
```bash
docker pull ghcr.io/a-brusnitsyn/financetracker:latest
docker run -p 8080:8080 ghcr.io/a-brusnitsyn/financetracker:latest
```

The `Dockerfile` uses a multi-stage build:
1. `maven:3.9-eclipse-temurin-17` → compiles & packages the app
2. `eclipse-temurin:17-jre-alpine` → lightweight runtime (~80MB)

---

## 🧪 Testing Strategy

| Test Type | Tool | Scope |
|-----------|------|-------|
| **Unit** | Mockito, AssertJ | Services, mappers, utilities |
| **Context** | `@SpringBootTest` + H2 | Bean wiring, JPA mapping, validation |
| **Integration** *(planned)* | Testcontainers + PostgreSQL | Real DB queries, Flyway migrations, security filters |

Run tests locally:
```bash
mvn clean test "-Dspring.profiles.active=test"
```

---

## 📁 Project Structure

```
src/main/java/org/brusnitsyn/financetracker/
├── config/          # Security, Web, JPA configs
├── controller/      # REST endpoints
├── service/         # Business logic
├── repository/      # JPA repositories
├── model/
│   ├── entity/      # Database entities
│   ├── dto/         # Request/Response objects
│   ├── enums/       # TransactionType, Role, etc.
│   └── mappers/     # MapStruct converters
├── exception/       # Custom exceptions + global handler
└── security/        # JWT filter, auth provider
```

---

💡 *Built for learning, optimized for production. Feedback & stars are highly appreciated!* ⭐
