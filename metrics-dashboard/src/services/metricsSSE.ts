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

    unsubscribe(id: string) {
        this.listeners.delete(id);

        // Only disconnect if no listeners remain
        if (this.listeners.size === 0) {
            this.disconnect();
        }
    }

    private setupEventSource() {
        const url = this.ALL_SERVICE_URL;

        try {
            this.eventSource = new EventSource(url);

            this.eventSource.onopen = () => {
                if (this.reconnectTimeout) {
                    clearTimeout(this.reconnectTimeout);
                    this.reconnectTimeout = null;
                }
            };

            this.eventSource.onerror = (error) => {
                console.error('SSE connection error:', error);
                console.error('EventSource readyState:', this.eventSource?.readyState);
                console.error('Connection URL:', url);
                this.disconnect();
                this.reconnectTimeout = setTimeout(() => {
                    this.setupEventSource();
                }, this.RECONNECT_DELAY);
            };

            this.eventSource.addEventListener('all-service-metrics', (event) => {
                try {
                    const data = JSON.parse(event.data);
                    this.listeners.forEach((callback, id) => {
                        callback(data);
                    });
                } catch (error) {
                    console.error('Error parsing SSE data:', error);
                    console.error('Raw event data:', event.data);
                }
            });
        } catch (error) {
            console.error('Error creating EventSource:', error);
            console.error('URL attempted:', url);
        }
    }
}

// Create singleton instance
export const metricsSSE = new MetricsSSEManager();
