import { Injectable, Inject } from '@nestjs/common';
import * as client from 'prom-client';
import * as mongoose from 'mongoose';

@Injectable()
export class MongoDBMetricsService {
  private readonly connectionPoolSizeGauge: client.Gauge<string>;
  private readonly activeConnectionsGauge: client.Gauge<string>;
  private readonly availableConnectionsGauge: client.Gauge<string>;

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

    setInterval(() => {
      this.collectConnectionPoolStats();
    }, 5000);
  }

  async collectConnectionPoolStats() {
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
}
