FROM node:20-bookworm-slim

WORKDIR /app

# Disable downloading Cypress desktop binary during container build
ENV CYPRESS_INSTALL_BINARY=0

# Enable Corepack for Yarn Modern (Berry)
RUN corepack enable

# Copy package definitions and yarn configuration
COPY package.json yarn.lock .yarnrc.yml ./
COPY .yarn ./.yarn

# Install dependencies using yarn berry
RUN yarn install

# Copy application files
COPY index.html vite.config.js eslint.config.js ./
COPY src ./src

EXPOSE 5173

CMD ["yarn", "dev", "--host", "0.0.0.0", "--port", "5173"]
