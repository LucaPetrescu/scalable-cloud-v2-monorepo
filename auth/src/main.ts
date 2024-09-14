/* eslint-disable */
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Registry } from 'prom-client';
import { HttpMetricsMiddleware } from './api/metrics/http-requests.middleware';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const registry = app.get('PROM_REGISTRY');
  app.use(
    new HttpMetricsMiddleware(registry).use.bind(
      new HttpMetricsMiddleware(registry),
    ),
  );

  await app.listen(3000);
}
bootstrap();
