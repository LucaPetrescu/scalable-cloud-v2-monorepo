import { useEffect, useState } from 'react';
import { metricsSSE } from '../../services/metricsSSE.ts';

export const useSystemMetrics = (service: string | undefined) => {
    const [metrics, setMetrics] = useState([]);

    console.log('useSystemMetrics is useSystemMetrics hook at the beginning');

    useEffect(() => {
        console.log('useSystemMetrics is useSystemMetrics hook inside useEffect for service: ', service);
        const listenerId = `system-${service}`;
        const handleMetricsData = (data: any) => {
            console.log(
                'useSystemMetrics is useSystemMetrics hook inside useEffect inside handleMetricsData: ',
                service,
                ' and data: ',
                data,
            );
            const systemMetrics = data[1]
                .filter((metric: any) => {
                    return ['"cpu_usage_percent"', '"ram_usage_percent"'].includes(metric.metricName);
                })
                .filter((metric: any) => metric.serviceName === service)
                .map((metric: any) => ({
                    ...metric,
                    metricName: metric.metricName.replace(/"/g, ''),
                }));
            console.log('systemMetrics for ', service, systemMetrics);
            setMetrics(systemMetrics);
        };

        metricsSSE.subscribe(listenerId, handleMetricsData);

        return () => {
            metricsSSE.unsubscribe(listenerId);
        };
    }, [service]);

    return metrics;
};
