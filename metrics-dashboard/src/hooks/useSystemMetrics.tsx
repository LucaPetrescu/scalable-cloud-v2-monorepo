import { useEffect, useState } from 'react';

export const useSystemMetrics = () => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        console.log('Setting up SSE connection for system metrics...');
        const eventSource = new EventSource('http://localhost:8085/metrics/sse/pushAuthServiceMetrics');

        eventSource.onopen = () => {
            console.log('SSE connection opened for system metrics');
        };

        eventSource.addEventListener('auth-service-metrics', (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log('Received system metrics data:', data);

                const systemMetrics = data
                    .filter((metric) => ['"cpu_usage_percent"', '"ram_usage_percent"'].includes(metric.metricName))
                    .map((metric) => ({
                        ...metric,
                        metricName: metric.metricName.replace(/"/g, ''),
                    }));

                setMetrics(systemMetrics);
            } catch (error) {
                console.error('Error parsing system metrics data:', error);
            }
        });

        eventSource.onerror = (err) => {
            console.error('SSE error for system metrics: ', err);
            eventSource.close();
        };

        return () => {
            console.log('Cleaning up SSE connection for system metrics...');
            eventSource.close();
        };
    }, []);

    return metrics;
};
