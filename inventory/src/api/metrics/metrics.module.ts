import { Module, Global } from '@nestjs/common';
import { Registry } from 'prom-client';
import { MetricsService } from './metrics.service';

@Global()
@Module({
  providers: [
    MetricsService,
    {
      provide: 'PROM_REGISTRY',
      useValue: new Registry(),
    },
  ],
  exports: [MetricsService],
})
export class MetricsModule {}
