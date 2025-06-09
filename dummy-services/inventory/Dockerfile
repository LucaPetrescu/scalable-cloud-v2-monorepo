FROM --platform=linux/arm64 node:18-alpine

WORKDIR /usr/src/app

# Install build dependencies
RUN apk add --no-cache python3 make g++

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

COPY .env ./

EXPOSE 3001

CMD ["npm", "run", "start"] 