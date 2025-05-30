import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useDatabaseMetrics = () => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
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
                .map((metric: any) => ({
                    ...metric,
                    metricName: metric.metricName.replace(/"/g, ''),
                }));
            setMetrics(systemMetrics);
        };

        metricsSSE.subscribe('database', handleMetricsData);

        return () => {
            metricsSSE.unsubscribe('database');
        };
    }, []);

    return metrics;
};
