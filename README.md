# E-Commerce API

A REST backend for a small online shop, built with Spring Boot as a learning project.
It covers the core flow of an e-commerce service: registration and login with JWT,
a product catalog, a shopping cart, checkout with stock control, order history and
payments through Stripe (test mode).

## Tech Stack

- Java 21, Spring Boot 4.1
- Spring Security with stateless JWT authentication (jjwt)
- Spring Data JPA / Hibernate, PostgreSQL, Flyway migrations
- Bean Validation
- Stripe Java SDK
- SpringDoc OpenAPI (Swagger UI)
- JUnit 5, Mockito, MockMvc, Testcontainers

## Features

- Registration and login; passwords hashed with BCrypt, JWT valid for 24 hours
- Roles `USER` and `ADMIN`; product management is admin-only
- Product catalog with search by name
- Shopping cart: add, view, remove items
- Checkout: creates an order from the cart, checks and reduces stock
- Order history for the current user
- Stripe PaymentIntent creation, payment confirmation and webhook handling
- Request validation and a single error format for all endpoints

## Design Decisions

**Price snapshot in orders.** `order_items` stores the price at the moment of checkout.
Order history keeps showing what the customer actually paid, even if the product
price changes later.

**Pessimistic locking on checkout.** Products are loaded with `PESSIMISTIC_WRITE`
(`SELECT ... FOR UPDATE`) while stock is checked and reduced, so two parallel
checkouts cannot sell the same last item twice.

**One error format: ProblemDetail (RFC 9457).** Every error is returned as
`application/problem+json`, including validation errors and the 401/403 responses
produced by the security filter chain (custom `AuthenticationEntryPoint` and
`AccessDeniedHandler`). Domain exceptions extend a common `ApiException` that
carries its HTTP status, grouped into 404 / 409 / 422 base classes. The 500 fallback
never exposes the exception message to the client.

```json
{
  "detail": "Not enough products on the storage",
  "instance": "/api/orders/checkout",
  "status": 422,
  "title": "Unprocessable Content"
}
```

**Stripe errors are translated at the service boundary.** `StripeException` is wrapped
into the application's own exceptions: a Stripe outage becomes `502 Bad Gateway`,
an invalid webhook signature becomes `400 Bad Request`. Controllers do not depend
on the Stripe SDK.

**Fewer database queries.** The JWT filter loads the user once per request and the
services work with that principal instead of loading the user again. All
`@ManyToOne` associations are `LAZY`, and `spring.jpa.open-in-view` is disabled:
entities are mapped to DTOs inside transactional services.

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL running on `localhost:5432` with a database named `e_commerce_api`
- Docker (only needed for the tests, Testcontainers starts its own PostgreSQL)

### Run locally

1. Set the environment variables:

   | Variable                | Required | Notes                                               |
   |-------------------------|----------|-----------------------------------------------------|
   | `DB_PASSWORD`           | yes      | password of the PostgreSQL user                     |
   | `DB_USERNAME`           | no       | defaults to `postgres`                              |
   | `DB_URL`                | no       | defaults to `jdbc:postgresql://localhost:5432/e_commerce_api` |
   | `JWT_SECRET`            | no       | Base64 key; a development default is provided       |
   | `STRIPE_API_KEY`        | no       | Stripe test key; needed only for payment endpoints  |
   | `STRIPE_WEBHOOK_SECRET` | no       | needed only for the webhook endpoint                |

2. Start the application with the `dev` profile:

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

   Flyway creates the schema on startup. The `dev` profile also seeds two users and
   three products into an empty database and logs SQL statements.

### Test users (`dev` profile only)

| Role  | Email             | Password    |
|-------|-------------------|-------------|
| ADMIN | admin@example.com | admin123    |
| USER  | test@example.com  | password123 |

### API documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`. Log in via `/api/auth/login`,
then paste the token into the **Authorize** dialog.

## API Overview

| Method | Endpoint                                | Access        | Description                    |
|--------|-----------------------------------------|---------------|--------------------------------|
| POST   | `/api/auth/register`                    | public        | Register, returns a JWT        |
| POST   | `/api/auth/login`                       | public        | Log in, returns a JWT          |
| GET    | `/api/products`                         | public        | List products                  |
| GET    | `/api/products/{id}`                    | public        | Get a product                  |
| GET    | `/api/products/search?query=`           | public        | Search products by name        |
| POST   | `/api/products`                         | ADMIN         | Create a product               |
| PUT    | `/api/products/{id}`                    | ADMIN         | Update a product               |
| DELETE | `/api/products/{id}`                    | ADMIN         | Delete a product               |
| GET    | `/api/cart`                             | authenticated | View the cart                  |
| POST   | `/api/cart/add`                         | authenticated | Add a product to the cart      |
| DELETE | `/api/cart/{productId}`                 | authenticated | Remove a product from the cart |
| POST   | `/api/orders/checkout`                  | authenticated | Create an order from the cart  |
| GET    | `/api/orders`                           | authenticated | Order history                  |
| POST   | `/api/payments/create-intent/{orderId}` | authenticated | Create a Stripe PaymentIntent  |
| POST   | `/api/payments/confirm`                 | authenticated | Confirm a payment              |
| POST   | `/api/payments/webhook`                 | Stripe        | Stripe webhook (signature checked) |

## Database Schema

```
users        id, email (unique), password, role
products     id, name, description, price, inventory
cart_items   id, quantity, user_id → users, product_id → products
orders       id, total_price, status, created_at, user_id → users
order_items  id, quantity, price, order_id → orders, product_id → products
```

Migrations live in `src/main/resources/db/migration/`.

## Tests

```bash
./gradlew test
```

The suite (about 50 tests) has three layers:

- **Unit tests** for services with Mockito, including Stripe calls mocked with `mockStatic`
- **Web layer tests** with `@WebMvcTest` and the real security configuration:
  status codes, validation errors, the ProblemDetail format, 401 vs 403
- **Integration tests** with Testcontainers PostgreSQL: repositories, and a full
  checkout over HTTP with a real JWT

Docker must be running for the integration tests.

## Known Limitations

This is a learning project; these points are known and not solved yet:

- Deleting a product that is referenced by an order fails with a database
  constraint error. The planned fix is a soft delete.
- Order history loads order items and products with extra queries (N+1);
  a `JOIN FETCH` or batch fetching would fix it.
- No pagination on product and order lists.
- Some endpoints do not follow REST conventions yet
  (`POST /api/cart/add`, product creation returns `200` instead of `201`).
- The Docker setup (`Dockerfile`, `docker-compose.yml`) is work in progress
  and not ready to use.
- No CI pipeline and no deployment yet.

## Project Structure

```
src/main/java/org/example/ecommerceapi/
├── config/       DataSeeder, OpenAPI configuration
├── controller/   REST controllers
├── dto/          request and response records
├── exception/    exception hierarchy and global handler
├── model/        JPA entities
├── repository/   Spring Data repositories
├── security/     JWT filter, JwtService, security configuration and handlers
└── service/      business logic
```
