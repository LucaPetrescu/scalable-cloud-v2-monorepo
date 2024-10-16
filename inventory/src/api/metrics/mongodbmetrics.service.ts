import { Injectable, Inject } from '@nestjs/common';
import * as client from 'prom-client';
import * as mongoose from 'mongoose';
import axios from 'axios';

export class MongoDBMetricsService {
  private readonly connectionPoolSizeGauge: client.Gauge<string>;
  private readonly activeConnectionsGauge: client.Gauge<string>;
  private readonly availableConnectionsGauge: client.Gauge<string>;
  private readonly queryTimeGauge: client.Gauge<string>;
  private readonly memoryUsageGauge: client.Gauge<string>;

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
  }

  private async collectConnectionPoolStats() {
    const connection = mongoose.connection;
    if (connection.readyState === 1) {
      try {
        const stats = await connection.db.admin().serverStatus();
      } catch (error) {}
    }
  }
}
