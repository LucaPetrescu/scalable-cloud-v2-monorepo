export interface MetricResponseDto {
    serviceName: string;
    metricName: string;
    value: number;
    timestamp: string;
    type: 'system' | 'network' | 'database';
}
