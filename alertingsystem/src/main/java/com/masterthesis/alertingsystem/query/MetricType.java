package com.masterthesis.alertingsystem.query;

public enum MetricType {

    CPU_USAGE("cpu_usage_percent", "CPU Usage", "%"),
    RAM_USAGE("ram_usage_percent", "RAM usage", "%"),
    HTTP_REQUESTS("http_requests_total", "HTTP Requests", "requests"),
    HTTP_DURATION("http_request_duration_seconds", "HTTP Request Duration", "seconds"),
    HTTP_REQUEST_DURATION_SECONDS_SUM("http_request_duration_seconds_sum", "HTTP Request Duration Seconds Sum", "seconds"),
    HTTP_REQUEST_DURATION_SECONDS_COUNT("http_request_duration_seconds_count", "HTTP Request Duration Seconds Count", "seconds"),
    MONGO_POOL_SIZE("mongo_connection_pool_size", "MongoDB Connection Pool Size", "connections"),
    MONGO_ACTIVE_CONNECTIONS("mongo_active_connections", "MongoDB Active Connections", "connections"),
    MONGO_AVAILABLE_CONNECTIONS("mongo_available_connections", "MongoDB Available Connections", "connections"),
    MONGO_QUERY_TIME("mongo_query_time_seconds", "MongoDB Query Time", "seconds"),
    MONGO_MEMORY_USAGE("mongo_memory_usage_bytes", "MongoDB Memory Usage", "bytes");

    private final String queryName;
    private final String displayName;
    private final String unit;

    MetricType(String queryName, String displayName, String unit) {
        this.queryName = queryName;
        this.displayName = displayName;
        this.unit = unit;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUnit() {
        return unit;
    }

    public static MetricType fromQueryName(String queryName) {
        for (MetricType metricType : MetricType.values()) {
            if (metricType.getQueryName().equals(queryName)) {
                return metricType;
            }
        }
        throw new IllegalArgumentException("Unknown metric: " + queryName);
    }
}
