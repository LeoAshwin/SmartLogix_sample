# SmartLogix Backend 🚚

SmartLogix is a production-ready, highly tested Spring Boot logistics backend. It is designed to manage the full lifecycle of last-mile delivery operations, including complex fulfillments, deterministic capacity-fit manifests, tracking events, carrier settlements, and automated proof of delivery.

## 🚀 Key Features

*   **Robust E2E Fulfillment Integration:** A rigorous state machine controlling package lifecycles (`PENDING` → `ASSIGNED` → `EN_ROUTE` → `DELIVERED`).
*   **Intelligent Manifesting System:** Custom capacity-fit dispatch grouping for vehicles (analyzing weight and volume dimensions).
*   **Carrier Settlements & Billing:** Deterministic multi-tier pricing calculation and dispute resolution with a standard fixed 8% commission engine.
*   **Automated Tracking & Returns Validation:** Robust append-only tracking systems and standardized reverse logistics configurations.
*   **Fully Tested System:** Extensive API coverage with 99 passing end-to-end API integrations, and robust JUnit5 Mockito business logic validations.

## 🛠️ Technology Stack

*   **Framework:** Spring Boot 3.4
*   **Language:** Java 21
*   **Database:** MySQL 8.0 (using Spring Data JPA)
*   **Documentation:** SpringDoc OpenAPI (Swagger UI)
*   **Data Mapping:** MapStruct & Lombok
*   **Testing:** JUnit 5, Mockito, PowerShell (for full E2E HTTP validation)

## 📦 Getting Started

### Prerequisites
*   Java 21 installed (`JAVA_HOME` configured).
*   Maven installed.
*   MySQL 8.0 installed and running on port 3306.

### Setup Database
1. Launch your MySQL server.
2. The application will leverage `hibernate.ddl-auto=update` during development, or create the database implicitly if absent.

### Configuration
The main configuration lives across several properties files, keeping environments separated cleanly:
*   `src/main/resources/application.properties` - Standard configuration for the application (server ports, JPA config).
*   `src/main/resources/application-local.properties` - Environment configurations specifically kept out of version control (e.g. database credentials). Make sure this file exists in your environment to run correctly:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/smartlogix?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=your_db_password
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```

### Running the Application

To start the application on the `local` profile, run the following:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
*The server will start on `http://localhost:8081` by default.*

## 🧪 Testing

The backend includes two completely functional test layers that provide rigid assertions on business operations:

1.  **Unit Tests (JUnit 5 & Mockito):** 
    Located in `src/test/java/...`, you can easily run unit tests bypassing the database to rapidly assert logic correctness.
    ```bash
    mvn clean test
    ```
2.  **Full End-to-End API Integration Suite (PowerShell):** 
    A robust script testing 99 individual lifecycles for all 16 domains. Make sure your server is running on port 8081 before executing.
    ```bash
    pwsh ./test_full_e2e.ps1
    ```

## 📖 API Documentation

SmartLogix makes use of OpenAPI standards to provide interactive endpoint documentation.
When the server is running, explore the Swagger UI at:
*   **Swagger UI:** `http://localhost:8081/swagger-ui.html`
*   **Raw OpenAPI JSON:** `http://localhost:8081/v3/api-docs`

---
*Built with Spring Boot and Java 21 for peak high performance.*
