💰 Finance Tracker API

A REST API for personal finance management: users, accounts, categories, and transactions.
Built with Java and Spring Boot, secured with JWT authentication, and containerized using Docker.

🚀 Tech Stack
Java 17
Spring Boot
Spring Security (JWT)
Spring Data JPA
PostgreSQL
Docker & Docker Compose
JUnit 5 & Mockito
Swagger (OpenAPI)
📦 Features
👤 Users
User registration
Authentication with JWT
💳 Accounts
Create accounts
Get all user accounts
Account balance is updated automatically
🏷 Categories
Create categories
Get user categories
Used for transactions
💸 Transactions
Create income and expense transactions
Automatic account balance update
Filtering
Pagination
🔐 Security
JWT-based authentication
Users can access only their own data
userId is not passed from the client — it is extracted from the JWT token
🗂 Project Structure
src/main/java/org/brusnitsyn/financetracker
├── config        // Security & Swagger configuration
├── controller    // REST controllers
├── service       // Business logic
├── repository    // JPA repositories
├── model
│   ├── entity    // JPA entities
│   ├── dto       // Request / Response DTOs
│   ├── mappers   // Entity ↔ DTO mappers
│   └── enums
├── exception     // Custom exceptions
└── filtr         // JWT filter
▶️ Running the Application (Docker)
1️⃣ Stop and remove existing containers and volumes
docker compose down -v
docker rmi finance-tracker-app || true
2️⃣ Build and start the application
docker compose up --build
3️⃣ Application will be available at

API:

http://localhost:8080

Swagger UI:

http://localhost:8080/swagger-ui.html
⚙️ Environment Variables

Configured via docker-compose.yml:

SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
JWT_EXPIRATION
🧪 Testing
Run unit tests
mvn test

Covered services:

AccountService
TransactionService
CategoryService

Tests are written using:

JUnit 5
Mockito
No Spring context (pure unit tests)
📑 API Examples
🔑 Authentication

POST /auth/register

{
  "email": "test@mail.com",
  "password": "123456"
}

POST /auth/login

{
  "email": "test@mail.com",
  "password": "123456"
}

Response:

{
  "token": "JWT_TOKEN"
}
💳 Create Account

POST /accounts

{
  "name": "Main",
  "currency": "USD",
  "balance": 1000
}
💸 Create Transaction

POST /transactions

{
  "accountId": 1,
  "categoryId": 2,
  "amount": 100,
  "date": "2026-04-01",
  "type": "EXPENSE",
  "description": "Food"
}
📌 Future Improvements
User roles (USER / ADMIN)
Refresh tokens
Integration tests
CI (GitHub Actions)
Frontend (React / Vue)
👨‍💻 Author

Aleksandr Brusnitsyn
Educational project to practice backend development with Java and Spring Boot.
