# ByteBites Microservices

## Table of Contents
1.  [Project Overview](#project-overview)
2.  [Architecture](#architecture)
    *   [Service Components](#service-components)
    *   [Technology Stack](#technology-stack)
3.  [Setup and Running the Application](#setup-and-running-the-application)
    *   [Prerequisites](#prerequisites)
    *   [Building the Services](#building-the-services)
    *   [Running with Docker Compose (Recommended)](#running-with-docker-compose-recommended)
    *   [Running Services Locally (Individual)](#running-services-locally-individual)
4.  [Configuration](#configuration)
    *   [Centralized Configuration (Config Server)](#centralized-configuration-config-server)
    *   [Service-Specific Configurations](#service-specific-configurations)
5.  [API Endpoints and Usage](#api-endpoints-and-usage)
    *   [Postman Collection](#postman-collection)
    *   [Authentication Service Endpoints](#authentication-service-endpoints)
    *   [Restaurant Service Endpoints](#restaurant-service-endpoints)
    *   [Order Service Endpoints](#order-service-endpoints)
6.  [Security Implementation](#security-implementation)
    *   [JWT Authentication Flow](#jwt-authentication-flow)
    *   [API Gateway Security](#api-gateway-security)
    *   [Auth Service Security](#auth-service-security)
7.  [Troubleshooting](#troubleshooting)
    *   [404 Not Found for Services](#404-not-found-for-services)
    *   [500 Internal Server Error (UnknownHostException)](#500-internal-server-error-unknownhostexception)
    *   [500 Internal Server Error (CORS Issue)](#500-internal-server-error-cors-issue)
    *   [401 Unauthorized (JWT Not Processed)](#401-unauthorized-jwt-not-processed)
    *   [Database Issues (H2)](#database-issues-h2)
    *   [Kafka Connectivity](#kafka-connectivity)
    *   [General Debugging](#general-debugging)
8.  [Further Documentation](#further-documentation)

---

## 1. Project Overview

ByteBites is a microservices-based application designed to manage restaurant and order functionalities, incorporating authentication and centralized configuration. It leverages Spring Boot and Spring Cloud to build a scalable and resilient system. The architecture includes an API Gateway for routing and security, a Eureka Server for service discovery, a Config Server for externalized configuration, and dedicated microservices for authentication, restaurants, and orders. Kafka is integrated for asynchronous event processing, specifically for order notifications.

## 2. Architecture

The application follows a microservices architectural pattern, with each service being independently deployable and scalable.

### Service Components

*   **Eureka Server (Port: 8761)**: Acts as a service registry, allowing microservices to register themselves and discover other services.
*   **Config Server (Port: 8888)**: Provides centralized configuration for all microservices, pulling properties from a Git repository (or local filesystem in this setup).
*   **API Gateway (Port: 8089)**: The single entry point for all client requests. It handles request routing to appropriate microservices and enforces JWT-based security for protected endpoints.
*   **Auth Service (Port: 8087)**: Manages user registration, login, and generates JWT (JSON Web Token) for authenticated users. It uses Spring Security for authentication and authorization.
*   **Restaurant Service (Port: 8081)**: Manages restaurant information, including menus.
*   **Order Service (Port: 8082)**: Handles order creation and management. It integrates with Kafka to publish order events.
*   **Notification Service (Port: 8083)**: (Placeholder/Consumer) Listens to Kafka order events to simulate notifications.
*   **Kafka & Zookeeper**: Used for asynchronous communication between services, particularly for order events. Managed via Docker Compose.

### Technology Stack

*   **Spring Boot**: Framework for building stand-alone, production-grade Spring applications.
*   **Spring Cloud**: Provides tools for building common patterns in distributed systems (Eureka, Config, Gateway).
*   **Spring Data JPA**: For database interaction with H2.
*   **Spring Security**: For authentication and authorization (JWT-based).
*   **JJWT**: Java JWT library for token creation and validation.
*   **Apache Kafka**: Distributed streaming platform for event-driven communication.
*   **H2 Database**: In-memory database used for development and testing.
*   **Lombok**: Reduces boilerplate code.
*   **ModelMapper**: Object mapping library.
*   **Maven**: Build automation tool.
*   **Docker & Docker Compose**: For containerization and orchestration of services.

## 3. Setup and Running the Application

### Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK) 17 or higher**
*   **Apache Maven 3.6.0 or higher**
*   **Docker Desktop** (includes Docker Engine and Docker Compose)

### Building the Services

Navigate to the root directory of the project (`C:/Users/ITCompliance/IdeaProjects/bytebites/`) and build all microservices using Maven:

```bash
mvn clean install
```

This command will compile all modules, run tests, and package them into executable JARs.

### Running with Docker Compose (Recommended)

This method simplifies the deployment and management of all services, including Kafka and Zookeeper.

1.  **Ensure Docker Desktop is running.**
2.  **Navigate to the project root directory:**
    ```bash
    cd C:/Users/ITCompliance/IdeaProjects/bytebites/
    ```
3.  **Start all services using Docker Compose:**
    ```bash
    docker-compose up -d
    ```
    This command will build and start all services in detached mode.
4.  **Verify services are running:**
    ```bash
    docker-compose ps
    ```
    You should see all services (zookeeper, kafka, config-server, eureka-server, auth-service, api-gateway, restaurant-service, order-service, notification-service) in a healthy state.

### Running Services Locally (Individual)

If you prefer to run services individually without Docker Compose (except for Kafka/Zookeeper if needed by services), follow these steps:

1.  **Start Kafka and Zookeeper (if required by services):**
    If your services rely on Kafka, you'll need to run Kafka and Zookeeper. You can still use `docker-compose up -d zookeeper kafka` from the project root to start just these two.

2.  **Start Config Server:**
    Navigate to `C:/Users/ITCompliance/IdeaProjects/bytebites/config-server/` and run:
    ```bash
    mvn spring-boot:run
    ```
    Wait for it to start on port `8888`.

3.  **Start Eureka Server:**
    Navigate to `C:/Users/ITCompliance/IdeaProjects/bytebites/eureka-server/` and run:
    ```bash
    mvn spring-boot:run
    ```
    Wait for it to start on port `8761`. You can access the Eureka dashboard at `http://localhost:8761/`.

4.  **Start Auth Service:**
    Navigate to `C:/Users/ITCompliance/IdeaProjects/bytebites/auth-service/` and run:
    ```bash
    mvn spring-boot:run
    ```
    Wait for it to start on port `8087`.

5.  **Start Restaurant Service:**
    Navigate to `C:/Users/ITCompliance/IdeaProjects/bytebites/restaurant-service/` and run:
    ```bash
    mvn spring-boot:run
    ```
    Wait for it to start on port `8081`.

6.  **Start Order Service:**
    Navigate to `C:/Users/ITCompliance/IdeaProjects/bytebites/order-service/` and run:
    ```bash
    mvn spring-boot:run
    ```
    Wait for it to start on port `8082`.

7.  **Start API Gateway:**
    Navigate to `C:/Users/ITCompliance/IdeaProjects/bytebites/api-gateway/` and run:
    ```bash
    mvn spring-boot:run
    ```
    Wait for it to start on port `8089`.

## 4. Configuration

### Centralized Configuration (Config Server)

The `config-server` loads configurations from the `config-repo` directory. Each service has its own `.properties` file (e.g., `api-gateway.properties`, `auth-service.properties`).

*   **`config-server/src/main/resources/application.properties`**:
    ```properties
    spring.application.name=config-server
    server.port=8888
    spring.profiles.active=native
    spring.cloud.config.server.native.search-locations=file:C:/Users/ITCompliance/IdeaProjects/bytebites/config-repo/
    eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
    eureka.instance.prefer-ip-address=true


## 5. API Endpoints and Usage

All API interactions should go through the API Gateway at `http://localhost:8089`.

### Postman Collection

A Postman collection is provided to easily test all endpoints, including the JWT authentication flow.

*   **File:** `ByteBites_Postman_Collection.json`
*   **Location:** `C:/Users/ITCompliance/IdeaProjects/bytebites/ByteBites_Postman_Collection.json`

**How to use the Postman Collection:**

1.  **Import:** Open Postman, click "Import" -> "File" -> "Upload Files", and select `ByteBites_Postman_Collection.json`.
2.  **Login First:** Expand the "Authentication Service" folder and run the **"Login User"** request. This request has a test script that automatically extracts the `accessToken` from the response and saves it as a collection variable.
3.  **Execute Other Requests:** Once the "Login User" request is successful, you can run any other request in the "Restaurant Service" or "Order Service" folders. They are configured to use the `{{accessToken}}` collection variable in their `Authorization` headers.

### Authentication Service Endpoints (via API Gateway)

*   **Register User:**
    *   `POST http://localhost:8089/api/v1/auth/register`
    *   Body:
        ```json
        {
          "firstName": "test",
          "lastName": "user",
          "email": "test@user.com",
          "password": "password"
        }
        ```
*   **Login User:**
    *   `POST http://localhost:8089/api/v1/auth/login`
    *   Body:
        ```json
        {
          "email": "test@user.com",
          "password": "password"
        }
        ```
    *   Response will contain `accessToken` and `refreshToken`.

### Restaurant Service Endpoints (via API Gateway)

All requests to the Restaurant Service (except for `/api/v1/auth/**`) require a valid JWT token in the `Authorization: Bearer <token>` header.

*   **Create Restaurant:** `POST http://localhost:8089/api/v1/restaurants`
*   **Get All Restaurants:** `GET http://localhost:8089/api/v1/restaurants`
*   **Get Restaurant by ID:** `GET http://localhost:8089/api/v1/restaurants/{id}`
*   **Update Restaurant:** `PUT http://localhost:8089/api/v1/restaurants/{id}`
*   **Delete Restaurant:** `DELETE http://localhost:8089/api/v1/restaurants/{id}`

### Order Service Endpoints (via API Gateway)

All requests to the Order Service require a valid JWT token in the `Authorization: Bearer <token>` header.

*   **Create Order:** `POST http://localhost:8089/api/v1/orders`
*   **Get All Orders:** `GET http://localhost:8089/api/v1/orders`
*   **Get Order by ID:** `GET http://localhost:8089/api/v1/orders/{id}`
*   **Update Order:** `PUT http://localhost:8089/api/v1/orders/{id}`
*   **Delete Order:** `DELETE http://localhost:8089/api/v1/orders/{id}`

## 6. Security Implementation

Security is implemented using JWT (JSON Web Tokens) and Spring Security, with a layered approach across the Auth Service and API Gateway.

### JWT Authentication Flow

1.  **User Registration/Login:** A user registers or logs in via the `Auth Service` (`/api/v1/auth/register` or `/api/v1/auth/login`).
2.  **Token Generation:** Upon successful login, the `Auth Service` generates an `accessToken` (JWT) and a `refreshToken`.
3.  **Client Stores Token:** The client (e.g., Postman, frontend application) receives and stores the `accessToken`.
4.  **Access Protected Resources:** For subsequent requests to protected resources (e.g., `/api/v1/restaurants`, `/api/v1/orders`), the client includes the `accessToken` in the `Authorization` header as `Bearer <accessToken>`.
5.  **API Gateway Validation:** The `API Gateway` intercepts the request. Its `JwtAuthenticationFilter` extracts the token and validates its signature and expiry using a shared secret. If valid, the request is forwarded.
6.  **Service-Level Authorization (Future Work):** While the API Gateway handles authentication, individual services (like Restaurant and Order) can implement their own granular authorization rules based on roles or other claims within the JWT (e.g., using `@PreAuthorize`).

### API Gateway Security

*   **Dependencies:** `spring-boot-starter-security`, `jjwt-api`, `jjwt-impl`, `jjwt-jackson` are added to `api-gateway/pom.xml`.
*   **`JwtTokenProvider`**: A simplified version of the one in `auth-service`, responsible only for validating tokens using the shared `app.jwt-secret`.
*   **`JwtAuthenticationFilter`**: A `WebFilter` that intercepts requests, validates JWTs, and sets the reactive security context.
*   **`SecurityConfig`**: Configures Spring Security for WebFlux:
    *   Disables CSRF.
    *   Permits access to `/api/v1/auth/**` and `/eureka/**`.
    *   Requires authentication for all other paths (`anyExchange().authenticated()`).
    *   The `JwtAuthenticationFilter` is automatically applied by Spring WebFlux due to it being a `@Component WebFilter`.

### Auth Service Security

*   **Dependencies:** `spring-boot-starter-security`, `jjwt-api`, `jjwt-impl`, `jjwt-jackson` are core.
*   **`SecurityConfig`**: Configures Spring Security for Servlet-based applications:
    *   Disables CSRF.
    *   Configures `JwtAuthenticationEntryPoint` for unauthorized access.
    *   Sets session management to stateless.
    *   Permits access to `/api/v1/auth/**` and requires authentication for others.
    *   Integrates `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
    *   Configures CORS to allow `allowedOriginPatterns("*")` with `allowCredentials(true)`.
*   **`JwtTokenProvider`**: Generates and validates JWT and refresh tokens.
*   **`CustomUserDetailsService`**: Loads user details from the database for Spring Security.
*   **`User.java`**: The JPA entity for users, including roles. The `JoinColumn` annotation for `user_roles` table is correctly set.

## 7. Troubleshooting

### 404 Not Found for Services

*   **Symptom:** Requests to `http://localhost:8089/api/v1/restaurants` (or orders) return 404.
*   **Cause:** The API Gateway might not be correctly routing the request, or the downstream service is not registered with Eureka or not running.
*   **Solution:**
    1.  Ensure all services (Config Server, Eureka Server, Auth Service, Restaurant Service, Order Service, API Gateway) are running.
    2.  Check Eureka dashboard (`http://localhost:8761/`) to confirm all services are registered and in `UP` status.
    3.  Verify `api-gateway.properties` has correct routing rules (`spring.cloud.gateway.routes`).

### 500 Internal Server Error (UnknownHostException)

*   **Symptom:** Logs show `UnknownHostException: Failed to resolve 'DESKTOP-XXXXXXX.mshome.net'` or similar.
*   **Cause:** Services are trying to register or discover each other using hostnames that are not resolvable in your network environment.
*   **Solution:** Ensure `eureka.instance.prefer-ip-address=true` and `eureka.instance.hostname=localhost` are set in the `config-repo` properties for `api-gateway.properties`, `auth-service.properties`, and `eureka-server.properties`. Restart all services after making these changes.

### 500 Internal Server Error (CORS Issue)

*   **Symptom:** Error message like "When allowCredentials is true, allowedOrigins cannot contain the special value "*"".
*   **Cause:** Incorrect CORS configuration in `Auth Service`'s `SecurityConfig.java`.
*   **Solution:** Ensure `allowedOriginPatterns("*")` is used instead of `allowedOrigins("*")` when `allowCredentials(true)` is set. This was addressed in `auth-service/src/main/java/com/tech/authservice/config/SecurityConfig.java`.

### 401 Unauthorized (JWT Not Processed)

*   **Symptom:** Requests to protected endpoints return `401 Unauthorized` with `WWW-Authenticate: Basic realm="Realm"`.
*   **Cause:** The `JwtAuthenticationFilter` in the API Gateway is not being correctly applied in the Spring Security WebFlux filter chain, causing requests to fall through to default basic authentication.
*   **Solution:** Ensure `api-gateway/src/main/java/com/tech/api_gateway/config/SecurityConfig.java` explicitly adds the `JwtAuthenticationFilter` using `.addFilterAt(jwtAuthenticationFilter, SecurityWebFilters.AUTHENTICATION)`. This was recently corrected.

### Database Issues (H2)

*   **Symptom:** Errors related to database connection or schema.
*   **Cause:** H2 database configuration issues.
*   **Solution:** Check `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, and `spring.jpa.hibernate.ddl-auto` in the respective service's properties files in `config-repo`. `ddl-auto=update` or `create-drop` should handle schema creation for H2.

### Kafka Connectivity

*   **Symptom:** Order events not being processed by Notification Service, or producer errors.
*   **Cause:** Kafka/Zookeeper not running, or incorrect `bootstrap-servers` configuration.
*   **Solution:**
    1.  Verify Kafka and Zookeeper containers are running and healthy (`docker-compose ps`).
    2.  Check `spring.kafka.consumer.bootstrap-servers` and `spring.kafka.producer.bootstrap-servers` in `notification-service.properties` and `order-service.properties` respectively. Ensure they point to the correct Kafka broker (e.g., `localhost:9092` or `kafka:9092` if running inside Docker Compose network).

### General Debugging

*   **Increase Logging:** For more detailed logs, set `logging.level.root=DEBUG` or specific package levels (e.g., `logging.level.com.tech.authservice=DEBUG`) in the service's properties file in `config-repo`.
*   **Check Service Logs:** Always review the console output or log files of the specific service that is returning an error. The stack trace provides valuable information.
*   **Postman Console:** Use the Postman Console (View -> Show Postman Console) to inspect request and response headers and bodies, which can help identify issues with tokens or content types.

## 8. Further Documentation

*   [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
*   [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.3/maven-plugin)
*   [Create an OCI image](https://docs.spring.io/spring-boot/3.5.3/maven-plugin/build-image.html)
*   [Spring Cloud Gateway Documentation](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
*   [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
*   [Spring Cloud Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)
*   [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/multi/multi__spring_cloud_config_server.html)
