import React from 'react';
import { useSystemMetrics } from '../../hooks/useSystemMetrics.tsx';
import { Card } from '../StatCards/Card.tsx';

export const SystemMetricsViews = () => {
    const metrics = useSystemMetrics();

    console.log('SystemMetricsViews received metrics:', metrics);

    const formatValue = (metric) => {
        console.log('Formatting value for metric:', metric);
        switch (metric.metricName) {
            case 'cpu_usage_percent':
            case 'ram_usage_percent':
                return `${Number(metric.metricValue).toFixed(2)}%`;
            default:
                return metric.metricValue.toString();
        }
    };

    const getTitle = (metricName) => {
        console.log('Getting title for metric:', metricName);
        switch (metricName) {
            case 'cpu_usage_percent':
                return 'CPU Usage';
            case 'ram_usage_percent':
                return 'RAM Usage';
            default:
                return metricName;
        }
    };

    return (
        <>
            {metrics.map((metric) => {
                console.log('Rendering card for metric:', metric);
                return (
                    <div key={metric.metricName} className="col-span-4">
                        <Card
                            title={getTitle(metric.metricName)}
                            value={formatValue(metric)}
                            period={`Last updated: ${new Date(metric.timestamp).toLocaleTimeString()}`}
                        />
                    </div>
                );
            })}
        </>
    );
};
