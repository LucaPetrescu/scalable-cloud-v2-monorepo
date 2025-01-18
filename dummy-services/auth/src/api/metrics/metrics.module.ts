import { Module, Global } from '@nestjs/common';
import { MetricsService } from './metrics.service';
import { Registry } from 'prom-client';
import { HttpMetricsService } from './http-metrics.service';
import { MongoDBMetricsService } from './mongodbmetrics.service';

@Global()
@Module({
  providers: [
    MetricsService,
    HttpMetricsService,
    MongoDBMetricsService,
    {
      provide: 'PROM_REGISTRY',
      useValue: new Registry(),
    },
  ],
  exports: [
    MetricsService,
    HttpMetricsService,
    MongoDBMetricsService,
    'PROM_REGISTRY',
  ],
})
export class MetricsModule {}
