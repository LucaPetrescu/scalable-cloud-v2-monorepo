FROM node:16-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

ENV MONGODB_URI mongodb://localhost:27018/products

EXPOSE 3001

CMD [ "npm", "run", "start:dev" ]