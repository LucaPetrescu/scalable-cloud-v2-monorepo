import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useSystemMetrics = (service: string | undefined) => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        const handleMetricsData = (data: any) => {
            const systemMetrics = data[1]
                .filter((metric: any) => ['"cpu_usage_percent"', '"ram_usage_percent"'].includes(metric.metricName))
                .map((metric: any) => ({
                    ...metric,
                    metricName: metric.metricName.replace(/"/g, ''),
                }));
            setMetrics(systemMetrics);
        };

        metricsSSE.subscribe('system', handleMetricsData);

        return () => {
            metricsSSE.unsubscribe('system');
        };
    }, []);

    return metrics;
};
