import {
  Injectable,
  Inject,
  OnModuleInit,
  NestMiddleware,
} from '@nestjs/common';
import * as client from 'prom-client';
import axios from 'axios';
import * as os from 'os';
import { Request, Response, NextFunction } from 'express';

@Injectable()
export class MetricsService implements OnModuleInit {
  //----------------------------------------------------------------------
  //Service Fields
  //----------------------------------------------------------------------

  //System metrics

  private readonly cpuUsageGauge: client.Gauge<string>;
  private readonly memoryUsageGauge: client.Gauge<string>;

  //----------------------------------------------------------------------
  //Constructor
  //----------------------------------------------------------------------

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
      name: 'memory_usage_percent',
      help: 'Memory usage in bytes',
      registers: [this.registry],
    });
  }

  //----------------------------------------------------------------------
  //Collecting initialization methods
  //----------------------------------------------------------------------

  onModuleInit() {
    this.startMonitoring();
    this.sendSystemMetricsToCollector();
  }

  //----------------------------------------------------------------------
  //Nest Middleware
  //----------------------------------------------------------------------

  /********************************************************************************** */

  private startMonitoring() {
    setInterval(() => {
      const cpuUsage = this.getCpuUsage();
      const memoryUsage = this.getMemoryUsage();

      this.cpuUsageGauge.set(cpuUsage.cpuUsage);
      this.memoryUsageGauge.set(memoryUsage.memoryUsage);
    }, 5000);
  }

  /********************************************************************************** */

  private async sendSystemMetricsToCollector() {
    setInterval(async () => {
      const metrics = await this.getMetrics();

      try {
        // await axios.post('http://localhost:8080/metrics', metrics, {
        //   headers: {
        //     'Content-Type': 'text/plain',
        //   },
        // });
        console.log('Metrics ', metrics);
      } catch (error) {
        console.error('Error sending metrics', error);
      }
    }, 10000);
  }

  //----------------------------------------------------------------------
  //Collecting methods
  //----------------------------------------------------------------------

  private getCpuUsage(): any {
    const usage = process.cpuUsage();
    const cpuUsage =
      (usage.user + usage.system) / (os.cpus().length * 1000 * 1000);
    console.log(`cpuUsage: ${cpuUsage}`);
    return { cpuUsage: cpuUsage };
  }

  /********************************************************************************** */

  private getMemoryUsage(): any {
    const usage = process.memoryUsage();
    const memoryUsage = usage.heapUsed / usage.heapTotal;
    console.log(`memoryUsage: ${memoryUsage}`);
    return { memoryUsage: memoryUsage };
  }

  /********************************************************************************** */
  private async getMetrics(): Promise<string> {
    return await this.registry.metrics();
  }
}
