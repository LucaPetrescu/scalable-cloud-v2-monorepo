import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useNetworkMetrics = (service: string | undefined) => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        const handleMetricsData = (data: any) => {
            const systemMetrics = data[1]
                .filter((metric: any) =>
                    ['"http_requests_total"', 'http_request_duration_seconds'].includes(metric.metricName),
                )
                .map((metric: any) => ({
                    ...metric,
                    metricName: metric.metricName.replace(/"/g, ''),
                }));
            setMetrics(systemMetrics);
        };

        metricsSSE.subscribe('network', handleMetricsData);

        return () => {
            metricsSSE.unsubscribe('network');
        };
    }, []);

    return metrics;
};
