# 🏦 Eagle Bank API

A simple REST API for a fictional bank built with **Spring Boot**.  
This project allows users to manage their profiles, bank accounts, and transactions (deposits & withdrawals).

---

## 🚀 Tech Stack

- Java 21
- Spring Boot (Web, Security, JPA, Validation)
- PostgreSQL
- Flyway (database migrations)
- JWT Authentication
- Testcontainers (integration testing)
- OpenAPI / Swagger UI

---

## 📌 Features

### 👤 Users
- Create a user
- Authenticate (JWT)
- Fetch, update, delete user

### 💳 Accounts
- Create bank account
- List user accounts
- Fetch, update, delete account

### 💸 Transactions
- Deposit money
- Withdraw money
- View transactions
- Fetch a specific transaction

> ⚠️ Transactions are immutable (cannot be updated or deleted)

---

## 🔐 Authentication

- JWT-based authentication
- Obtain a token via:



---

## 📖 API Documentation

Swagger UI available at:

- http://localhost:8080/swagger-ui.html


---

## 🛠️ Running the App

### 1. Start PostgreSQL (or use Docker)

### 2. Run the application

- mvn spring-boot:run



