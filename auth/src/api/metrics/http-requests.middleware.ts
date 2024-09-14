import { Injectable, Inject, NestMiddleware } from '@nestjs/common';

import { Request, Response, NextFunction } from 'express';

import * as client from 'prom-client';

@Injectable()
export class HttpMetricsMiddleware implements NestMiddleware {
  private readonly httpRequestCounter: client.Counter<string>;
  private readonly httpRequestDurationHistogram: client.Histogram<string>;

  constructor(
    @Inject('PROM_REGISTRY') private readonly registry: client.Registry,
  ) {
    if (!this.registry.getSingleMetric('http_requests_total')) {
      this.httpRequestCounter = new client.Counter({
        name: 'http_requests_total',
        help: 'Total number of HTTP requests',
        labelNames: ['method', 'route', 'status_code'],
        registers: [this.registry],
      });
    }

    if (!this.registry.getSingleMetric('http_request_duration_seconds')) {
      this.httpRequestDurationHistogram = new client.Histogram({
        name: 'http_request_duration_seconds',
        help: 'Duration of HTTP requests in seconds',
        labelNames: ['method', 'route', 'status_code'],
        registers: [this.registry],
      });
    }
  }

  use(req: Request, res: Response, next: NextFunction): void {
    const startTime = Date.now();
    const { method, path: route } = req;

    res.on('finish', () => {
      const duration = (Date.now() - startTime) / 1000;
      const statusCode = res.statusCode;

      this.httpRequestCounter
        .labels(method, route, statusCode.toString())
        .inc();

      this.httpRequestDurationHistogram
        .labels(method, route, statusCode.toString())
        .observe(duration);
    });

    next();
  }

  async sendHttpMetricToCollector() {
    try {
      const httpRequestsTotal = this.registry.getSingleMetric(
        'http_requests_total',
      );

      const httpRequestDurationSeconds = this.registry.getSingleMetric(
        'http_request_duration_seconds',
      );

      console.log(httpRequestsTotal, httpRequestDurationSeconds);
    } catch (error) {}
  }
}
