class MetricsSSEManager {
    private eventSource: EventSource | null = null;
    private reconnectTimeout: NodeJS.Timeout | null = null;
    private listeners: Map<string, (data: any) => void> = new Map();
    private readonly RECONNECT_DELAY = 1000;
    private readonly SSE_URL = 'http://localhost:8085/metrics/sse/pushAuthServiceMetrics';

    subscribe(id: string, callback: (data: any) => void) {
        this.listeners.set(id, callback);

        // Start connection if this is the first subscriber
        if (this.listeners.size === 1) {
            this.connect();
        }
    }

    unsubscribe(id: string) {
        this.listeners.delete(id);

        // Close connection if no more subscribers
        if (this.listeners.size === 0) {
            this.disconnect();
        }
    }

    private connect() {
        console.log('Setting up shared SSE connection...');
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

    private setupEventSource() {
        if (this.eventSource) {
            this.eventSource.close();
        }

        this.eventSource = new EventSource(this.SSE_URL);

        this.eventSource.onopen = () => {
            console.log('Shared SSE connection opened');
            if (this.reconnectTimeout) {
                clearTimeout(this.reconnectTimeout);
                this.reconnectTimeout = null;
            }
        };

        this.eventSource.addEventListener('auth-service-metrics', (event) => {
            try {
                const data = JSON.parse(event.data);
                // Broadcast to all subscribers
                this.listeners.forEach((callback) => callback(data));
            } catch (error) {
                console.error('Error parsing metrics data:', error);
            }
        });

        this.eventSource.onerror = (err) => {
            console.error('Shared SSE error:', err);
            this.eventSource?.close();

            if (!this.reconnectTimeout && this.listeners.size > 0) {
                this.reconnectTimeout = setTimeout(() => {
                    console.log('Attempting to reconnect shared SSE...');
                    this.setupEventSource();
                }, this.RECONNECT_DELAY);
            }
        };
    }
}

// Create singleton instance
export const metricsSSE = new MetricsSSEManager();
