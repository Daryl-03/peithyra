# Peithyra

Peithyra is a live debate platform designed to make online discussions more structured, readable, and engaging.

Two participants debate a topic through controlled speaking turns while an audience follows the exchange. The first version will focus on text-based debates.

## Project structure

```text
peithyra/
├── backend/       # Spring Boot API
├── frontend/      # Next.js web application
├── .github/       # GitHub Actions workflows
└── README.md
```

## Tech stack

### Backend

- Java 25
- Spring Boot 4.1
- Gradle with Kotlin DSL
- PostgreSQL
- Flyway
- Testcontainers

### Frontend

- Next.js
- TypeScript
- App Router
- Tailwind CSS
- Biome
- pnpm

### Tooling

- GitHub Actions
- SonarQube Cloud
- Docker Compose

## Requirements

- JDK 25
- Node.js 24
- pnpm
- Docker
- Git

## Run the backend

```bash
cd backend
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

On Linux or macOS:

```bash
./gradlew bootRun
```

The backend runs by default at:

```text
http://localhost:8080
```

## Run the frontend

```bash
cd frontend
pnpm install
pnpm dev
```

The frontend runs by default at:

```text
http://localhost:3000
```

## Run the checks

### Backend

```bash
cd backend
./gradlew build
```

On Windows:

```powershell
cd backend
.\gradlew.bat build
```

### Frontend

```bash
cd frontend
pnpm lint
pnpm build
```

## Project status

Peithyra is currently in its initial development phase.

The current focus is establishing the technical foundation before implementing the first debate features.