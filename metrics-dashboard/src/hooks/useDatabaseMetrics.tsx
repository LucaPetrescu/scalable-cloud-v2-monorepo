import { useEffect, useState } from 'react';

export const useDatabaseMetrics = () => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        console.log('Setting up SSE connection for database metrics...');
        const eventSource = new EventSource('http://localhost:8085/metrics/sse/pushAuthServiceMetrics');

        eventSource.onopen = () => {
            console.log('SSE connection opened for database metrics');
        };

        eventSource.addEventListener('auth-service-metrics', (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log('Received database metrics data:', data);

                const databaseMetrics = data
                    .filter((metric) =>
                        [
                            '"mongo_connection_pool_size"',
                            '"mongo_active_connections"',
                            '"mongo_available_connections"',
                        ].includes(metric.metricName),
                    )
                    .map((metric) => ({
                        ...metric,
                        metricName: metric.metricName.replace(/"/g, ''),
                    }));

                setMetrics(databaseMetrics);
            } catch (error) {
                console.error('Error parsing database metrics data:', error);
            }
        });

        eventSource.onerror = (err) => {
            console.error('SSE error for database metrics: ', err);
            eventSource.close();
        };

        return () => {
            console.log('Cleaning up SSE connection for database metrics...');
            eventSource.close();
        };
    }, []);

    return metrics;
};
