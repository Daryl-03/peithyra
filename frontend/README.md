# Peithyra frontend

Next.js application using TypeScript, Tailwind CSS, Kinde authentication and Biome.

## Requirements

- Node.js 24.
- pnpm 11.18.0 (declared in `package.json`).
- A running API and a configured Kinde application.

Run all commands below from `frontend/`.

## Environment

Configure `frontend/.env.local` (or the existing `frontend/.env`):

```dotenv
BACKEND_URL=http://localhost:8080
KINDE_ISSUER_URL=https://your-tenant.kinde.com
KINDE_CLIENT_ID=your-client-id
KINDE_CLIENT_SECRET=your-client-secret
KINDE_SITE_URL=http://localhost:3000
KINDE_POST_LOGIN_REDIRECT_URL=http://localhost:3000/api/auth/complete
KINDE_POST_LOGOUT_REDIRECT_URL=http://localhost:3000
```

Use your Kinde application values and register the callback URL `http://localhost:3000/api/auth/kinde_callback` and the matching redirect URLs. Keep real secrets out of Git. `.env.local` takes precedence over `.env`.

`BACKEND_URL` is used by server-side requests. Use `http://localhost:8080` when Next.js runs on your machine; Compose uses `http://backend:8080` between containers. Next.js does not automatically load the repository-root `.env`.

## Development

```bash
pnpm install --frozen-lockfile
pnpm dev
```

Open [http://localhost:3000](http://localhost:3000). To start the API and PostgreSQL with Docker, use the [root README](../README.md).

## Checks and tests

```bash
# Check formatting, lint rules and imports
pnpm lint

# Apply automatic fixes
pnpm lint:fix

# Format files
pnpm format

# Type-check without emitting files
pnpm exec tsc --noEmit

# Run tests in watch mode
pnpm test

# Run tests once
pnpm test --run

# Run one test file
pnpm test --run src/features/debates/api/api.test.ts
```

## Production build

```bash
pnpm build
pnpm start
```

Configure the environment before building and starting. The server uses port 3000 by default. The Docker image uses the standalone Next.js output and starts it with `node server.js`.

## Docker

From `frontend/`, build the frontend image through the shared Compose configuration:

```bash
docker compose -f ../compose.yaml -f ../compose-local.yaml build frontend
```

See the [Docker commands](../README.md#docker-operations) for startup, logs and shutdown. The current Compose files do not publish the frontend port to the host.
