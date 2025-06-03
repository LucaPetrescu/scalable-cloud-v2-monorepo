import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useNetworkMetrics = (service: string | undefined) => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        const listenerId = `network-${service}`;
        const handleMetricsData = (data: any) => {
            const systemMetrics = data[1]
                .filter((metric: any) =>
                    ['"http_requests_total"', 'http_request_duration_seconds'].includes(metric.metricName),
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
