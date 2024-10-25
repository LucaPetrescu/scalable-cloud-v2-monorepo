import { Module, Global } from '@nestjs/common';
import { Registry } from 'prom-client';
import { MetricsService } from './metrics.service';
import { HttpMetricsService } from './http-metrics.service';

@Global()
@Module({
  providers: [
    MetricsService,
    HttpMetricsService,
    {
      provide: 'PROM_REGISTRY',
      useValue: new Registry(),
    },
  ],
  exports: [MetricsService, HttpMetricsService],
})
export class MetricsModule {}
