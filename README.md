# URL Shortener REST API

A Java Spring application that provides a REST API to shorten URL.
This project enables users to create short URLs for long links, manage those links,
and track usage statistics like click counts.

## Features

### 1. **Short URL Management**

- Generate unique short URLs for given long URLs.
- Allow users to define custom short codes (if not already in use).
- Track click statistics for each URL.
- URLs can have an optional expiration date.
- Support for active and expired URL statuses.

### 2. **Authentication & Security**

- User authentication using **JWT (JSON Web Tokens)**.
- Endpoints for user registration and login:
    - `/api/v1/signup` - Register a new user.
    - `/api/v1/login` - Authenticate a user and generate a JWT.
- Passwords are securely hashed using **BCryptPasswordEncoder**.
- Stateless, token-based session management.

### 3. **REST API**

- Fully-documented API using **Springdoc OpenAPI**.
- RESTful endpoints for creating, reading, updating, and deleting URLs.
- Pagination support for listing URLs.

### 4. **Error Handling**

- Comprehensive exception handling with meaningful error messages and HTTP status codes.
- Validation errors are captured and returned with details for easier debugging.


---

## Technologies Used

- **Java 21**: Core programming language for the application.
- **Spring Boot 3.4.1**: Simplifies application development with embedded server and configuration support.
- **Spring Data JPA**: Database access and management.
- **Spring Security**: Provides secure user authentication and authorization.
- **PostgreSQL**: Database for persisting URLs and user information.
- **Flyway**: Handles database schema migrations.
- **Jakarta Bean Validation**: Ensures data integrity.
- **JWT**: Stateless, token-based session handling.
- **Lombok**: Reduces boilerplate code.
- **JUnit 5 & Mockito**: Unit and integration testing frameworks.
- **Testcontainers**: Provides isolated and lightweight test environments using containers.

---

## Prerequisites

- **Java 21**: Ensure Java 21 is installed.
- **Gradle**: For dependency management and build automation.
- **Docker & Docker Compose**: Used for containerized development and deployment.
- **PostgreSQL**: Database for URL and user data.


## Getting Started

### Installation

1. Clone the repository:

```shell
git clone <repository-url>
cd <repository-directory>
```

2. Set up environment variables:

```shell
cp .env.example .env
```

Fill in `.env` with:
- **JWT_SECRET**: Secret key for token signing.
- **DB_USER**, **DB_PASS**, and **DB_URL**: Database connection details.

3. Build and run the application using Docker Compose:

```shell
docker-compose up --build
```

4. Access the application:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Redirect links: `http://localhost:8080/s/{shortCode}`

---

## API Endpoints

### Authentication

- `POST /api/v1/signup`: Register a new user.
- `POST /api/v1/login`: Authenticate a user and generate a JWT.

### URL Management

- `GET /api/v1/urls`: List all URLs (possible to list active, expired or all urls).
- `GET /api/v1/urls/{id}`: Retrieve details for a specific URL by ID.
- `GET /api/v1/urls/shortCode/{shortCode}`: Retrieve details for a specific URL by its short code.
- `POST /api/v1/urls`: Create a new short URL.
- `PUT /api/v1/urls/{id}`: Update an existing URL.
- `DELETE /api/v1/urls/{id}`: Delete a URL.

### Redirect

- `GET /s/{shortCode}`: Redirect to the original URL.

## Future Enhancements

- **Custom Expiration Dates**: Allow users to set custom expiration periods for their short URLs.
- **Admin Role**: Add administrative features for managing users and URLs.
- **Enhanced Analytics**: Provide detailed analytics, such as geographic locations of clicks.
- **Custom Domains**: Support custom domains for short URLs.
- **Soft Deletion**: Archive URLs instead of permanently deleting them.