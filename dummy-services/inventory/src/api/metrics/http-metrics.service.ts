import { Injectable, Inject } from '@nestjs/common';
import axios from 'axios';
import * as client from 'prom-client';

@Injectable()
export class HttpMetricsService {
  private readonly httpRequestCounter: client.Counter<string>;
  private readonly httpRequestDurationHistogram: client.Histogram<string>;

  constructor(
    @Inject('PROM_REGISTRY') private readonly registry: client.Registry,
  ) {
    this.httpRequestCounter = new client.Counter({
      name: 'http_requests_total',
      help: 'Total number of HTTP requests',
      labelNames: ['method', 'route', 'status_code'],
      registers: [this.registry],
    });

    this.httpRequestDurationHistogram = new client.Histogram({
      name: 'http_request_duration_seconds',
      help: 'Duration of HTTP requests in seconds',
      labelNames: ['method', 'route', 'status_code'],
      registers: [this.registry],
    });
  }

  incrementRequestCounter(
    method: string,
    route: string,
    statusCode: number,
    duration: number,
  ): void {
    this.httpRequestCounter.labels(method, route, statusCode.toString()).inc();
    this.httpRequestDurationHistogram
      .labels(method, route, statusCode.toString())
      .observe(duration);
  }

  async getHttpRequestCount(): Promise<string> {
    return await this.registry.getSingleMetricAsString('http_requests_total');
  }

  async getHttpRequestDuration(): Promise<string> {
    return await this.registry.getSingleMetricAsString(
      'http_request_duration_seconds',
    );
  }
}
