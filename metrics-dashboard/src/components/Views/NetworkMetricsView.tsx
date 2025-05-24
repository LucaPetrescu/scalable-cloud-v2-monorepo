import React from 'react';
import { useNetworkMetrics } from '../../hooks/metrics/useNetworkMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';

export const NetworkMetricsView = () => {
    const metrics = useNetworkMetrics();

    const formatValue = (metric) => {
        switch (metric.metricName) {
            case 'http_requests_total':
                return metric.metricValue.toLocaleString();
            default:
                return metric.metricValue.toString();
        }
    };

    const getTitle = (metricName) => {
        switch (metricName) {
            case 'http_requests_total':
                return 'HTTP Requests';
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
