import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import * as client from 'prom-client';
import axios from 'axios';
import * as os from 'os';

@Injectable()
export class MetricsService implements OnModuleInit {
  private readonly cpuUsageGauge: client.Gauge<string>;
  private readonly memoryUsageGauge: client.Gauge<string>;

  constructor(
    @Inject('PROM_REGISTRY') private readonly registry: client.Registry,
  ) {
    this.registry = new client.Registry();

    this.cpuUsageGauge = new client.Gauge({
      name: 'cpu_usage_percent',
      help: 'CPU usage percentage',
      registers: [this.registry],
    });

    this.memoryUsageGauge = new client.Gauge({
      name: 'ram_usage_percent',
      help: 'RAM usage percentage',
      registers: [this.registry],
    });
  }

  onModuleInit() {
    this.startMonitoring();
  }

  private startMonitoring() {
    setInterval(() => {
      const cpuUsage = this.getCpuUsage();
      const memoryUsage = this.getMemoryUsage();

      this.cpuUsageGauge.set(cpuUsage.cpuUsage);
      this.memoryUsageGauge.set(memoryUsage.memoryUsage);
    }, 5000);
  }

  private getCpuUsage(): any {
    const usage = process.cpuUsage();
    const cpuUsage =
      (usage.user + usage.system) / (os.cpus().length * 1000 * 1000);
    console.log(`cpuUsage: ${cpuUsage}`);
    return { cpuUsage: cpuUsage };
  }

  private getMemoryUsage(): any {
    const usage = process.memoryUsage();
    const memoryUsage = usage.heapUsed / usage.heapTotal;

    console.log(`memoryUsage: ${memoryUsage}`);
    return { memoryUsage: memoryUsage };
  }
}
