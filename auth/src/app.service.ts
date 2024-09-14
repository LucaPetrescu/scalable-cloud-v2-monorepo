/* eslint-disable */
import { Injectable } from '@nestjs/common';
import { MetricsService } from './api/metrics/metrics.service';
@Injectable()
export class AppService {
  getHello(): string {
    return 'Hello World!';
  }
}
