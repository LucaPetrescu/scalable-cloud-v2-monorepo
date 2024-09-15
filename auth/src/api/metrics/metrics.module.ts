import { Module, Global } from '@nestjs/common';
import { MetricsService } from './metrics.service';
import { Registry } from 'prom-client';
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
  exports: [MetricsService, HttpMetricsService, 'PROM_REGISTRY'],
})
export class MetricsModule {}
