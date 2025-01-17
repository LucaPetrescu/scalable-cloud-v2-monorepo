import { Injectable, NestMiddleware } from '@nestjs/common';
import { HttpMetricsService } from '../metrics/http-metrics.service';

@Injectable()
export class HttpMetricsMiddleware implements NestMiddleware {
  constructor(private httpMetricsService: HttpMetricsService) {}

  use(req: any, res: any, next: () => void) {
    const startTime = Date.now();

    res.on('finish', () => {
      const durationInSeconds = (Date.now() - startTime) / 1000;
      const { method, path: route } = req;
      const statusCode = res.statusCode;

      this.httpMetricsService.incrementRequestCounter(
        method,
        route,
        statusCode,
        durationInSeconds,
      );
    });

    next();
  }
}
