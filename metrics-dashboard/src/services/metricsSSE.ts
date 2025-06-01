import { allServiceMetricsUrl } from '../utils/routes.ts';

class MetricsSSEManager {
    private eventSource: EventSource | null = null;
    private reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
    private listeners: Map<string, (data: any) => void> = new Map();
    private readonly RECONNECT_DELAY = 1000;
    private readonly ALL_SERVICE_URL = allServiceMetricsUrl;

    subscribe(id: string, callback: (data: any) => void) {
        this.listeners.set(id, callback);

        // Start connection if this is the first subscriber
        if (this.listeners.size === 1) {
            this.connect();
        }
    }

    private connect() {
        console.log('Setting up combined SSE connection...');
        this.setupEventSource();
    }

    private disconnect() {
        if (this.reconnectTimeout) {
            clearTimeout(this.reconnectTimeout);
            this.reconnectTimeout = null;
        }
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
    }

    unsubscribe() {
        this.listeners.clear();
        this.disconnect();
    }

    private setupEventSource() {
        const url = this.ALL_SERVICE_URL;
        console.log(`Creating combined service connection:`, url);

        this.eventSource = new EventSource(url);

        this.eventSource.onopen = () => {
            console.log(`Combined service connection opened`);
            if (this.reconnectTimeout) {
                clearTimeout(this.reconnectTimeout);
                this.reconnectTimeout = null;
            }
        };

        this.eventSource.onerror = (error) => {
            console.error('Combined service connection error:', error);
            this.disconnect();
            // Attempt to reconnect after delay
            this.reconnectTimeout = setTimeout(() => {
                console.log('Attempting to reconnect to combined service...');
                this.setupEventSource();
            }, this.RECONNECT_DELAY);
        };

        this.eventSource.addEventListener('all-service-metrics', (event) => {
            try {
                const data = JSON.parse(event.data);
                // data: [MetricResponseDto, ...]
                console.log('Received all-service-metrics data:', data);
                // Broadcast to all subscribers
                this.listeners.forEach((callback) => callback(data));
            } catch (error) {
                console.error('Error parsing all-service-metrics data:', error);
            }
        });
    }
}

// Create singleton instance
export const metricsSSE = new MetricsSSEManager();
