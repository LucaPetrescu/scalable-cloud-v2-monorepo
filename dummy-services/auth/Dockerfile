FROM --platform=linux/arm64 node:18-alpine

WORKDIR /usr/src/app

# Install build dependencies
RUN apk add --no-cache python3 make g++

# Copy package files
COPY package*.json ./

# Debug: Show node and npm versions
RUN node --version && npm --version

# Clean install with verbose output
RUN npm cache clean --force && \
    rm -rf node_modules && \
    npm install --verbose && \
    # Debug: List bcrypt files
    ls -la node_modules/bcrypt/lib/binding/napi-v3/ && \
    # Rebuild bcrypt with verbose output
    npm rebuild bcrypt --build-from-source --verbose

# Copy source code
COPY . .

COPY .env ./

EXPOSE 3000

CMD ["npm", "run", "start"]