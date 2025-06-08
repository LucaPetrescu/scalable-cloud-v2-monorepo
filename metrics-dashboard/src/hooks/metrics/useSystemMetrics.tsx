import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useSystemMetrics = (service: string | undefined) => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        const listenerId = `system-${service}`;
        const handleMetricsData = (data: any) => {
            const systemMetrics = data[1]
                .filter((metric: any) => {
                    return ['"cpu_usage_percent"', '"ram_usage_percent"'].includes(metric.metricName);
                })
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
