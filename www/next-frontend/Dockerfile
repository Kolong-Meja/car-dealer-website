# ===================== BASE IMAGE =====================
FROM oven/bun:1.3.11 AS base
WORKDIR /app

# ===================== DEPENDENCIES =====================
FROM base AS deps

# Copy dependency files
COPY package.json bun.lock ./

# Install dependencies
RUN bun install --frozen-lockfile

# ===================== BUILD =====================
FROM base AS builder

COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Disable telemetry during build
ENV NEXT_TELEMETRY_DISABLED=1

RUN bun run build

# ===================== PRODUCTION =====================
FROM node:22-alpine AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Create non-root user (security best practice)
RUN addgroup -g 1001 -S app \
  && adduser -S faisalnext -u 1001

# Copy only required build output
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

USER faisalnext

EXPOSE 3000

CMD ["node", "server.js"]