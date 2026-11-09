# Peithyra API

Spring Boot API using Java 25, PostgreSQL, Flyway and Gradle with Kotlin DSL.

## Requirements

- JDK 25.
- Docker for PostgreSQL and Testcontainers integration tests.

Gradle is provided by the wrapper. Run the commands below from `backend/`.

## Database and environment

Start only PostgreSQL when running the API with Gradle:

```bash
docker compose -f ../compose.yaml -f ../compose-local.yaml up -d postgres
```

Set these environment variables in your terminal or IDE run configuration:

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/peithyra
SPRING_DATASOURCE_USERNAME=peithyra
SPRING_DATASOURCE_PASSWORD=peithyra
KINDE_ISSUER_URI=https://your-tenant.kinde.com
KINDE_JWK_SET_URI=https://your-tenant.kinde.com/.well-known/jwks.json
```

Match the database credentials to the root Compose `.env` and use your Kinde tenant. Spring Boot does not automatically load that `.env` when launched with Gradle.

For example, in PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/peithyra"
$env:SPRING_DATASOURCE_USERNAME = "peithyra"
$env:SPRING_DATASOURCE_PASSWORD = "peithyra"
$env:KINDE_ISSUER_URI = "https://your-tenant.kinde.com"
$env:KINDE_JWK_SET_URI = "https://your-tenant.kinde.com/.well-known/jwks.json"
```

On Linux/macOS, set each variable with `export NAME=value` instead. Flyway applies migrations from `src/main/resources/db/migration` at startup.

## Gradle commands

| Action | Windows PowerShell | Linux/macOS |
| --- | --- | --- |
| Start the API | `.\gradlew.bat bootRun` | `./gradlew bootRun` |
| Run all tests | `.\gradlew.bat test` | `./gradlew test` |
| Run debate application tests | `.\gradlew.bat test --tests "com.peithyra.api.debate.application.*"` | `./gradlew test --tests "com.peithyra.api.debate.application.*"` |
| Run the JPA integration tests | `.\gradlew.bat test --tests "com.peithyra.api.debate.adapters.JpaDebateRepositoryIntegrationTest"` | `./gradlew test --tests "com.peithyra.api.debate.adapters.JpaDebateRepositoryIntegrationTest"` |
| Clean, build and test | `.\gradlew.bat clean build` | `./gradlew clean build` |
| Build the executable JAR | `.\gradlew.bat bootJar` | `./gradlew bootJar` |
| Generate coverage | `.\gradlew.bat jacocoTestReport` | `./gradlew jacocoTestReport` |

Testcontainers requires a running Docker engine and creates its own test database; it does not need the Compose database. `bootJar` packages the application without running tests.

Reports:

- Tests: `build/reports/tests/test/index.html`.
- Coverage: `build/reports/jacoco/test/html/index.html`.
- JARs: `build/libs/`.

The API listens on [http://localhost:8080](http://localhost:8080). Health endpoint: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

## Run the API in Docker

Instead of `bootRun`, use:

```bash
docker compose -f ../compose.yaml -f ../compose-local.yaml up -d --build backend
docker compose -f ../compose.yaml -f ../compose-local.yaml logs -f backend
```

This also starts PostgreSQL and injects the environment from the root Compose configuration. Do not run the Docker API and `bootRun` on port 8080 at the same time.

See the [root README](../README.md) for the Compose environment, service status and shutdown commands.
