        # Recur — Subscription Tracker

Recur is a backend API for tracking recurring subscriptions. It helps you see **when each subscription renews**, how much you pay **monthly (and overall)**, and stay ahead of upcoming charges.

## Features (planned / in progress)

- Track subscriptions with renewal dates and pricing
- View monthly spend across all active subscriptions
- Get visibility into upcoming renewals
- Secure user accounts with JWT-based authentication and refresh sessions

## Tech stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot |
| Security | Spring Security + JWT (JJWT) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL |
| Build | Maven |

## Project status

The auth foundation is in place:

- User and session models
- JWT access tokens with refresh-token rotation
- Stateless Spring Security filter chain
- CORS configuration for a frontend client

Subscription domain APIs (CRUD, renewals, spend summaries) are the next layer to build on top of this.

## Prerequisites

- **JDK 17+**
- **Maven 3.9+** (or use the included Maven Wrapper)
- **MySQL 8+** running locally (or reachable from your machine)

## Getting started

### 1. Clone and configure

```bash
git clone <repo-url>
cd subscription-tracker
```

Update `src/main/resources/application.properties` (or preferably environment / secret management) with your local values:

| Property | Purpose |
| --- | --- |
| `spring.datasource.url` | MySQL JDBC URL |
| `spring.datasource.username` | DB user |
| `spring.datasource.password` | DB password |
| `jwt.secret` | Base64-encoded signing key for JWTs |
| `cors.allowed-origins` | Frontend origin(s) allowed by CORS |

> **Security note:** Do not commit real passwords or JWT secrets. Prefer environment variables or a secrets manager for anything sensitive.

### 2. Create the database

The default JDBC URL uses `createDatabaseIfNotExist=true`, so MySQL can create the `subscription-tracker` schema on first connect. Hibernate `ddl-auto=update` will create/update tables from entities.

### 3. Run the app

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The API is served under the context path:

```text
http://localhost:8080/api/v1
```

Public auth routes are expected under `/api/v1/auth/**`. All other routes require a valid JWT.

### 4. Run tests

```bash
./mvnw test
```

## Configuration overview

| Key | Description |
| --- | --- |
| `server.servlet.context-path` | API prefix (`/api/v1`) |
| `jwt.accessTokenExpirationTime` | Access token lifetime (ms) |
| `jwt.refreshTokenExpirationTime` | Refresh / session lifetime (ms) |
| `cors.allowed-origins` | Allowed browser origins |
| `cors.allowed-methods` | Allowed HTTP methods |
| `cors.allowed-headers` | Allowed request headers |

## Project layout

```text
src/main/java/com/Recur/subscription_tracker/
├── configuration/   # Security and CORS
├── filter/          # JWT authentication filter
├── model/           # JPA entities (User, Session, …)
├── repository/      # Spring Data repositories
├── service/         # Business logic
└── utils/           # Helpers (e.g. JwtUtil)
```

## License

Private / unpublished — all rights reserved unless otherwise stated.
