FROM node:20-bookworm-slim

WORKDIR /app

# Install openssl and build tools for native addons (e.g., argon2)
RUN apt-get update && apt-get install -y openssl python3 make g++ && rm -rf /var/lib/apt/lists/*

# Copy dependency definitions and prisma schema first for layer caching
COPY package.json yarn.lock ./
COPY src/data/schema.prisma ./src/data/schema.prisma

# Install dependencies
RUN yarn install --frozen-lockfile

# Generate Prisma Client
RUN npx prisma generate

# Copy project files
COPY tsconfig.json ./
COPY config ./config
COPY src ./src
COPY docker-entrypoint.sh ./

RUN chmod +x docker-entrypoint.sh

EXPOSE 9000

ENTRYPOINT ["./docker-entrypoint.sh"]
CMD ["npx", "ts-node", "./src/index.ts"]
