FROM node:16-alpine

WORKDIR /usr/src/app

COPY package*.json ./

# RUN npm install

RUN apk update && apk add --no-cache python3 make g++ && npm install

COPY . .

ENV MONGODB_URI mongodb://localhost:27017/users

EXPOSE 3000

CMD ["npm", "run", "start:dev"]