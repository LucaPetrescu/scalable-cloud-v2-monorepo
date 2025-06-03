import { allServiceMetricsUrl } from '../utils/routes.ts';

class MetricsSSEManager {
    private eventSource: EventSource | null = null;
    private reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
    private listeners: Map<string, (data: any) => void> = new Map();
    private readonly RECONNECT_DELAY = 1000;
    private readonly ALL_SERVICE_URL = allServiceMetricsUrl;

    subscribe(id: string, callback: (data: any) => void) {
        console.log(`Subscribing to metrics with id: ${id}, URL: ${this.ALL_SERVICE_URL}`);
        this.listeners.set(id, callback);

        // Start connection if this is the first subscriber
        if (this.listeners.size === 1) {
            console.log('First subscriber, connecting to SSE...');
            this.connect();
        } else {
            console.log(`Total subscribers: ${this.listeners.size}`);
        }
    }

    private connect() {
        console.log('Setting up SSE connection to:', this.ALL_SERVICE_URL);
        this.setupEventSource();
    }

    private disconnect() {
        console.log('Disconnecting SSE connection');
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
        console.log(`Unsubscribing listener: ${id}`);
        this.listeners.delete(id);

        // Only disconnect if no listeners remain
        if (this.listeners.size === 0) {
            console.log('No more listeners, disconnecting SSE');
            this.disconnect();
        } else {
            console.log(`Remaining listeners: ${this.listeners.size}`);
        }
    }

    private setupEventSource() {
        const url = this.ALL_SERVICE_URL;
        console.log(`Creating new EventSource for URL:`, url);

        try {
            this.eventSource = new EventSource(url);

            this.eventSource.onopen = () => {
                console.log(`SSE connection opened successfully to ${url}`);
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
                // Attempt to reconnect after delay
                this.reconnectTimeout = setTimeout(() => {
                    console.log('Attempting to reconnect to SSE...');
                    this.setupEventSource();
                }, this.RECONNECT_DELAY);
            };

            this.eventSource.addEventListener('all-service-metrics', (event) => {
                try {
                    console.log('Raw SSE event received:', event);
                    const data = JSON.parse(event.data);
                    console.log('Parsed SSE data:', data);
                    console.log('Number of listeners:', this.listeners.size);
                    // Broadcast to all subscribers
                    this.listeners.forEach((callback, id) => {
                        console.log(`Calling callback for listener: ${id}`);
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
