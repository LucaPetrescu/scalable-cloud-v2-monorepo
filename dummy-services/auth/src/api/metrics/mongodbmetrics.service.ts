/* eslint-disable prettier/prettier */
import { Injectable, Inject } from '@nestjs/common';
import * as client from 'prom-client';
import * as mongoose from 'mongoose';
import axios from 'axios';

@Injectable()
export class MongoDBMetricsService {
  private readonly connectionPoolSizeGauge: client.Gauge<string>;
  private readonly activeConnectionsGauge: client.Gauge<string>;
  private readonly availableConnectionsGauge: client.Gauge<string>;
  private readonly queryTimeGauge: client.Gauge<string>;
  private readonly memoryUsageGauge: client.Gauge<string>;

  private metricsCollectorUrl: string = 'http://192.168.0.171:8080/auth';

  constructor(
    @Inject('PROM_REGISTRY') private readonly registry: client.Registry,
  ) {
    this.connectionPoolSizeGauge = new client.Gauge({
      name: 'mongo_connection_pool_size',
      help: 'Size of MongoDB connection pool',
      registers: [this.registry],
    });

    this.activeConnectionsGauge = new client.Gauge({
      name: 'mongo_active_connections',
      help: 'Number of active MongoDB connections',
      registers: [this.registry],
    });

    this.availableConnectionsGauge = new client.Gauge({
      name: 'mongo_available_connections',
      help: 'Number of available MongoDB connections',
      registers: [this.registry],
    });

    this.queryTimeGauge = new client.Gauge({
      name: 'mongo_query_time_seconds',
      help: 'Execution tome for MongoDB queries',
      registers: [this.registry],
    });

    this.memoryUsageGauge = new client.Gauge({
      name: 'mongo_memory_usage_bytes',
      help: 'MongoDB memory usage in bytes',
      registers: [this.registry],
    });

    this.initMiddleware();

    setInterval(() => {
      this.collectConnectionPoolStats();
      this.collectMemoryUsage();
    }, 5000);

    setInterval(() => {
      this.sendMongoDBMetricsToCollector();
    }, 10000);
  }

  private async collectConnectionPoolStats() {
    const connection = mongoose.connection;
    if (connection.readyState === 1) {
      try {
        const stats = await connection.db.admin().serverStatus();
        const poolStats = stats.connections;

        this.connectionPoolSizeGauge.set(poolStats.current);
        this.activeConnectionsGauge.set(
          poolStats.current - poolStats.available,
        );
        this.availableConnectionsGauge.set(poolStats.available);
      } catch (error) {
        console.error('Error reconnecting to MongoDB:', error);
      }
    }
  }

  private initMiddleware() {
    mongoose.plugin((schema) => {
      schema.pre('find', () => {
        this['start'] = process.hrtime();
      });

      schema.post('find', () => {
        const [seconds, nanoseconds] = process.hrtime(this['start']);
        const executionTime = (seconds * nanoseconds) / 1e9;

        this.queryTimeGauge.set(executionTime);
      });
    });
  }

  private async collectMemoryUsage() {
    const connection = mongoose.connection;
    if (connection.readyState === 1) {
      try {
        const stats = await connection.db.admin().serverStatus();
        const memoryUsage = stats.mem.resident;
        this.memoryUsageGauge.set(memoryUsage);
      } catch (error) {
        console.error('Error reconnecting to MongoDB:', error);
      }
    }
  }

  private async getConnectionPoolSize(): Promise<string> {
    return await this.registry.getSingleMetricAsString(
      'mongo_connection_pool_size',
    );
  }

  private async getActiveConnections(): Promise<string> {
    return await this.registry.getSingleMetricAsString(
      'mongo_active_connections',
    );
  }

  private async getAvailableConnections(): Promise<string> {
    return await this.registry.getSingleMetricAsString(
      'mongo_available_connections',
    );
  }

  private async getQueryTime(): Promise<string> {
    return await this.registry.getSingleMetricAsString(
      'mongo_query_time_seconds',
    );
  }

  private async getMemoryUsage(): Promise<string> {
    return await this.registry.getSingleMetricAsString(
      'mongo_memory_usage_bytes',
    );
  }

  private async sendMongoDBMetricsToCollector() {
    const connectionPoolSize = await this.getConnectionPoolSize();
    const activeConnections = await this.getActiveConnections();
    const availableConnections = await this.getAvailableConnections();
    const queryTime = await this.getQueryTime();
    const memoryUsage = await this.getMemoryUsage();

    try {
      axios.post(
        `${this.metricsCollectorUrl}/mongodb-metrics/connection-pool-size`,
        connectionPoolSize,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        },
      );

      axios.post(
        `${this.metricsCollectorUrl}/mongodb-metrics/active-connections`,
        activeConnections,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        },
      );

      axios.post(
        `${this.metricsCollectorUrl}/mongodb-metrics/available-connections`,
        availableConnections,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        },
      );

      axios.post(
        `${this.metricsCollectorUrl}/mongodb-metrics/query-time`,
        queryTime,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        },
      );

      axios.post(
        `${this.metricsCollectorUrl}/mongodb-metrics/memory-usage`,
        memoryUsage,
        {
          headers: {
            'Content-Type': 'text/plain',
          },
        },
      );
    } catch (e) {
      console.error(e);
    }
  }
}
