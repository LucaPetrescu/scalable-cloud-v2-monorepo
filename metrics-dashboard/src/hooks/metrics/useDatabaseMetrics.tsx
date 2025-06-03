import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useDatabaseMetrics = (service: string | undefined) => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        const listenerId = `database-${service}`;
        const handleMetricsData = (data: any) => {
            const systemMetrics = data[1]
                .filter((metric: any) =>
                    [
                        '"mongo_connection_pool_size"',
                        '"mongo_active_connections"',
                        '"mongo_available_connections"',
                        '"mongo_query_time_seconds"',
                        '"mongo_memory_usage_bytes"',
                    ].includes(metric.metricName),
                )
                .filter((metric: any) => metric.serviceName === service)
                .map((metric: any) => ({
                    ...metric,
                    metricName: metric.metricName.replace(/"/g, ''),
                }));
            setMetrics(systemMetrics);
        };

        metricsSSE.subscribe(listenerId, handleMetricsData);

        return () => {
            metricsSSE.unsubscribe(listenerId);
        };
    }, [service]);

    return metrics;
};
