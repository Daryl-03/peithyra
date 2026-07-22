# Peithyra API

Backend API for **Peithyra**, a live debate platform.

## Tech stack

- Java 25
- Spring Boot 4.1
- Gradle with Kotlin DSL
- PostgreSQL
- Flyway
- Testcontainers

## Requirements

- JDK 25
- Docker
- Git

The project uses the Gradle Wrapper, so Gradle does not need to be installed globally.

## Run the application

Start the local database:

```bash
docker compose up -d
```

Start the backend:

### Windows

```powershell
.\gradlew.bat bootRun
```

### Linux and macOS

```bash
./gradlew bootRun
```

The API runs by default at:

```text
http://localhost:8080
```

## Run the tests

### Windows

```powershell
.\gradlew.bat test
```

### Linux and macOS

```bash
./gradlew test
```

## Build the project

### Windows

```powershell
.\gradlew.bat clean build
```

### Linux and macOS

```bash
./gradlew clean build
```

## Health check

```text
GET http://localhost:8080/actuator/health
```

## Project structure

```text
src/
├── main/
│   ├── java/com/peithyra/api/
│   └── resources/
└── test/
```

## Status

Initial project setup.