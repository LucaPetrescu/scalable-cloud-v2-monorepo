import { useEffect, useState } from 'react';

let eventSource: EventSource | null = null;
let reconnectTimeout: NodeJS.Timeout | null = null;
const RECONNECT_DELAY = 1000;

const createEventSource = () => {
    if (eventSource) {
        eventSource.close();
    }
    eventSource = new EventSource('http://localhost:8085/metrics/sse/pushAuthServiceMetrics');
    return eventSource;
};

export const useDatabaseMetrics = () => {
    const [metrics, setMetrics] = useState([]);

    useEffect(() => {
        console.log('Setting up SSE connection for database metrics...');

        const setupEventSource = () => {
            const es = createEventSource();

            es.onopen = () => {
                console.log('SSE connection opened for database metrics');
                if (reconnectTimeout) {
                    clearTimeout(reconnectTimeout);
                    reconnectTimeout = null;
                }
            };

            es.addEventListener('auth-service-metrics', (event) => {
                try {
                    const data = JSON.parse(event.data);
                    const databaseMetrics = data[1]
                        .filter((metric) =>
                            [
                                '"mongo_connection_pool_size"',
                                '"mongo_active_connections"',
                                '"mongo_available_connections"',
                                '"mongo_query_time_seconds"',
                                '"mongo_memory_usage_bytes"',
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

            es.onerror = (err) => {
                console.error('SSE error for database metrics: ', err);
                es.close();

                if (!reconnectTimeout) {
                    reconnectTimeout = setTimeout(() => {
                        console.log('Attempting to reconnect...');
                        setupEventSource();
                    }, RECONNECT_DELAY);
                }
            };
        };

        setupEventSource();

        return () => {
            if (reconnectTimeout) {
                clearTimeout(reconnectTimeout);
            }
            if (eventSource) {
                eventSource.close();
                eventSource = null;
            }
        };
    }, []);

    return metrics;
};
