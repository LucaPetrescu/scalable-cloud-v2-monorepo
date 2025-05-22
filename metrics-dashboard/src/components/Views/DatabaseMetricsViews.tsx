import React from 'react';
import { useDatabaseMetrics } from '../../hooks/useDatabaseMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';

export const DatabaseMetricsViews = () => {
    const metrics = useDatabaseMetrics();

    const formatValue = (metric) => {
        switch (metric.metricName) {
            case 'mongo_connection_pool_size':
            case 'mongo_active_connections':
                return metric.metricValue.toLocaleString();
            case 'mongo_available_connections':
                return `${metric.metricValue}`;
            default:
                return metric.metricValue.toString();
        }
    };

    const getTitle = (metricName) => {
        console.log('Getting title for metric:', metricName);
        switch (metricName) {
            case 'mongo_connection_pool_size':
                return 'Connection Pool Size';
            case 'mongo_active_connections':
                return 'Active Connections';
            case 'mongo_available_connections':
                return 'Available Connections';
            default:
                return metricName;
        }
    };

    return (
        <>
            {metrics.map((metric) => (
                <div key={metric.metricName} className="col-span-4">
                    <Card
                        title={getTitle(metric.metricName)}
                        value={formatValue(metric)}
                        period={`Last updated: ${new Date(metric.timestamp).toLocaleTimeString()}`}
                    />
                </div>
            ))}
        </>
    );
};
