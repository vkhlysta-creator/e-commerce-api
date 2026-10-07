# E-Commerce API

A production-ready RESTful e-commerce backend built with Spring Boot. Features JWT authentication, Stripe payment integration, role-based access control, and Docker support.

## Tech Stack

- **Java 21** + **Spring Boot 4.1**
- **Spring Security** — JWT-based stateless authentication
- **Spring Data JPA** + **Hibernate** — ORM with pessimistic locking on checkout
- **PostgreSQL** — primary database
- **Flyway** — database migrations
- **Stripe** — payment processing with webhook support
- **Testcontainers** — integration tests against a real PostgreSQL container
- **Docker** + **Docker Compose** — containerized deployment
- **SpringDoc OpenAPI** — auto-generated Swagger UI

## Features

- User registration and login with JWT tokens
- Role-based access control (`USER` / `ADMIN`)
- Product catalog with search by name
- Shopping cart management (add, view, remove items)
- Checkout with inventory validation and pessimistic locking to prevent race conditions
- Order history
- Stripe payment intent creation and webhook handling
- Input validation on all endpoints
- Global exception handling with structured error responses

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose

### Run with Docker

1. Clone the repository:
```bash
git clone https://github.com/vkhlysta-creator/e-commerce-api.git
cd e-commerce-api
```

2. Create a `.env` file in the project root:
```env
DB_URL=jdbc:postgresql://db:5432/ecommerce_db
DB_USERNAME=ecommerce_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_base64_encoded_secret_key

STRIPE_API_KEY=sk_test_your_stripe_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
```

3. Start the application:
```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

### Run Locally

1. Make sure PostgreSQL is running locally on port `5432` with a database named `e_commerce_api`.

2. Set the required environment variables:
```bash
export DB_PASSWORD=your_password
export JWT_SECRET=your_base64_encoded_secret
export STRIPE_API_KEY=sk_test_your_key
export STRIPE_WEBHOOK_SECRET=whsec_your_secret
```

3. Run the application:
```bash
./gradlew bootRun -Dspring.profiles.active=local
```

> Running with the `local` profile activates the `DataSeeder`, which populates the database with two test users and three products on first startup.

### Test Credentials (local/dev profile only)

| Role  | Email                 | Password      |
|-------|-----------------------|---------------|
| ADMIN | admin@example.com     | admin123      |
| USER  | test@example.com      | password123   |

## API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html` — all endpoints are documented and can be tested directly from the browser. Use the Authorize button to provide your JWT token.

## API Overview

### Auth — `/api/auth`

| Method | Endpoint            | Access  | Description         |
|--------|---------------------|---------|---------------------|
| POST   | `/api/auth/register` | Public | Register a new user |
| POST   | `/api/auth/login`    | Public | Login, returns JWT  |

### Products — `/api/products`

| Method | Endpoint                        | Access      | Description              |
|--------|---------------------------------|-------------|--------------------------|
| GET    | `/api/products`                 | Public      | Get all products         |
| GET    | `/api/products/{id}`            | Public      | Get product by ID        |
| GET    | `/api/products/search?query=`   | Public      | Search products by name  |
| POST   | `/api/products`                 | ADMIN only  | Create a new product     |
| PUT    | `/api/products/{id}`            | ADMIN only  | Update a product         |
| DELETE | `/api/products/{id}`            | ADMIN only  | Delete a product         |

### Cart — `/api/cart`

| Method | Endpoint                    | Access         | Description                  |
|--------|-----------------------------|----------------|------------------------------|
| GET    | `/api/cart`                 | Authenticated  | View current cart            |
| POST   | `/api/cart/add`             | Authenticated  | Add a product to cart        |
| DELETE | `/api/cart/{productId}`     | Authenticated  | Remove a product from cart   |

### Orders — `/api/orders`

| Method | Endpoint              | Access         | Description                          |
|--------|-----------------------|----------------|--------------------------------------|
| POST   | `/api/orders/checkout`| Authenticated  | Place an order from current cart     |
| GET    | `/api/orders`         | Authenticated  | Get current user's order history     |

### Payments — `/api/payments`

| Method | Endpoint                              | Access         | Description                          |
|--------|---------------------------------------|----------------|--------------------------------------|
| POST   | `/api/payments/create-intent/{orderId}` | Authenticated | Create a Stripe PaymentIntent      |
| POST   | `/api/payments/confirm`               | Authenticated  | Confirm a payment by intent ID       |
| POST   | `/api/payments/webhook`               | Public         | Stripe webhook handler               |

## Database Schema

```
users
  id, email (unique), password, role

products
  id, name, description, price, inventory

cart_items
  id, quantity, user_id → users, product_id → products

orders
  id, total_price, status, created_at, user_id → users

order_items
  id, quantity, price, order_id → orders, product_id → products
```

Schema is managed by Flyway — migrations are in `src/main/resources/db/migration/`.

## Running Tests

```bash
./gradlew test
```

Integration tests use Testcontainers to spin up a real PostgreSQL instance — no manual setup required. Docker must be running.

## Project Structure

```
src/main/java/org/example/ecommerceapi/
├── config/          — DataSeeder, OpenAPI config
├── controller/      — REST controllers
├── dto/             — Request/response records
├── exception/       — Custom exceptions + global handler
├── model/           — JPA entities
├── repository/      — Spring Data JPA repositories
├── security/        — JWT filter, JwtService, SecurityConfig
└── service/         — Business logic
```
