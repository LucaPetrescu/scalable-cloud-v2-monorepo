import { useEffect, useState } from 'react';

export const useNetworkMetrics = () => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        console.log('Setting up SSE connection for network metrics...');
        const eventSource = new EventSource('http://localhost:8085/metrics/sse/pushAuthServiceMetrics');

        eventSource.onopen = () => {
            console.log('SSE connection opened for network metrics');
        };

        eventSource.addEventListener('auth-service-metrics', (event) => {
            try {
                const data = JSON.parse(event.data);

                // Filter only network-related metrics
                const networkMetrics = data
                    .filter((metric) => ['"http_requests_total"'].includes(metric.metricName))
                    .map((metric) => ({
                        ...metric,
                        metricName: metric.metricName.replace(/"/g, ''), // Remove quotes from metric name
                    }));

                setMetrics(networkMetrics);
            } catch (error) {
                console.error('Error parsing network metrics data:', error);
            }
        });

        eventSource.onerror = (err) => {
            console.error('SSE error for network metrics: ', err);
            eventSource.close();
        };

        return () => {
            console.log('Cleaning up SSE connection for network metrics...');
            eventSource.close();
        };
    }, []);

    return metrics;
};
