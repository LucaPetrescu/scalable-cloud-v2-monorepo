import { Module, Global } from '@nestjs/common';
import { MetricsService } from './metrics.service';
import { Registry } from 'prom-client';

@Global()
@Module({
  providers: [
    MetricsService,
    {
      provide: 'PROM_REGISTRY',
      useValue: new Registry(),
    },
  ],
  exports: [MetricsService, 'PROM_REGISTRY'],
})
export class MetricsModule {}
