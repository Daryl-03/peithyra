# Peithyra

Peithyra is a live debate platform with structured speaking turns. The first version focuses on text-based debates.

## Project structure

- [Backend](backend/README.md): Java 25, Spring Boot, PostgreSQL, Flyway and Gradle commands.
- [Frontend](frontend/README.md): Next.js, TypeScript, pnpm and Biome commands.
- `compose.yaml`: PostgreSQL, API and frontend containers.
- `compose-local.yaml`: additional localhost ports for PostgreSQL and the API.

## Requirements

- Docker with Docker Compose v2.
- For development outside containers: JDK 25, Node.js 24 and pnpm 11.18.0.

## Docker configuration

Run the following commands from the repository root. Compose reads the root `.env` file. Configure these variables with your local Kinde application values:

```dotenv
POSTGRES_DB=peithyra
POSTGRES_USER=peithyra
POSTGRES_PASSWORD=peithyra
KINDE_ISSUER_URI=https://your-tenant.kinde.com
KINDE_JWK_SET_URI=https://your-tenant.kinde.com/.well-known/jwks.json
BACKEND_URL=http://backend:8080
KINDE_ISSUER_URL=https://your-tenant.kinde.com
KINDE_CLIENT_ID=your-client-id
KINDE_CLIENT_SECRET=your-client-secret
KINDE_SITE_URL=http://localhost:3000
KINDE_POST_LOGIN_REDIRECT_URL=http://localhost:3000/api/auth/complete
KINDE_POST_LOGOUT_REDIRECT_URL=http://localhost:3000
```

Register the matching callback and redirect URLs in Kinde. Keep real secrets out of Git. The PostgreSQL defaults above are for local development.

## Local development with Docker

Start PostgreSQL and the API, then run the frontend locally using its [README](frontend/README.md):

```bash
docker compose -f compose.yaml -f compose-local.yaml up -d --build backend
```

The API is available at `http://localhost:8080`; PostgreSQL is available at `localhost:5432`. Starting `backend` also starts its PostgreSQL dependency.

To run both applications outside Docker, start only PostgreSQL and follow the two application READMEs:

```bash
docker compose -f compose.yaml -f compose-local.yaml up -d postgres
```

## Docker operations

```bash
# Start or rebuild all three services
docker compose -f compose.yaml -f compose-local.yaml up -d --build

# Inspect service status
docker compose -f compose.yaml -f compose-local.yaml ps

# Follow API logs (Ctrl+C stops following, not the container)
docker compose -f compose.yaml -f compose-local.yaml logs -f backend

# Follow frontend logs
docker compose -f compose.yaml -f compose-local.yaml logs -f frontend

# Rebuild the API after code changes
docker compose -f compose.yaml -f compose-local.yaml up -d --build backend

# Stop services without removing containers
docker compose -f compose.yaml -f compose-local.yaml stop

# Remove containers and the network; retain PostgreSQL data
docker compose -f compose.yaml -f compose-local.yaml down
```

The current local override publishes only ports 8080 and 5432. The containerized frontend listens on port 3000 inside Docker but is not published to the host. Use `pnpm dev` for the browser-accessible local frontend, or configure port publishing/reverse proxy access separately.

`compose.yaml` alone publishes no host ports. PostgreSQL data is stored in the named volume `peithyra-postgres-data`. Adding `--volumes` to `down` deletes that database data.

The Docker image builds do not run the test suites. See the application READMEs for validation commands.
