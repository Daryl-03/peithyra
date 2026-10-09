# Base stage
FROM node:24-alpine AS base

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"

RUN corepack enable


# Dependencies stage
FROM base AS dependencies

WORKDIR /app

COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./

RUN pnpm install --frozen-lockfile


# Builder stage
FROM base AS builder

WORKDIR /app

COPY --from=dependencies /app/node_modules ./node_modules
COPY . .

ENV NEXT_TELEMETRY_DISABLED=1

# Kinde validates configuration during module loading. These placeholders are
# only for the build; real configuration is injected into the runner by Compose.
RUN KINDE_ISSUER_URL=https://build.example.invalid \
    KINDE_CLIENT_ID=build-placeholder \
    KINDE_CLIENT_SECRET=build-placeholder \
    KINDE_SITE_URL=http://localhost:3000 \
    KINDE_POST_LOGIN_REDIRECT_URL=http://localhost:3000/api/auth/complete \
    KINDE_POST_LOGOUT_REDIRECT_URL=http://localhost:3000 \
    pnpm build


# Runtime stage
FROM node:24-alpine AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV HOSTNAME=0.0.0.0
ENV PORT=3000

RUN addgroup --system --gid 1001 nodejs \
    && adduser --system --uid 1001 nextjs

COPY --from=builder --chown=nextjs:nodejs /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

CMD ["node", "server.js"]
