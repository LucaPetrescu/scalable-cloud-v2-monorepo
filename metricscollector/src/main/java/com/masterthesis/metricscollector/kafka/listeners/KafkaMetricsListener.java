package com.masterthesis.metricscollector.kafka.listeners;

import com.masterthesis.metricscollector.utils.Utils;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KafkaMetricsListener {

    // SYSTEM METRICS
    private static final Gauge cpuMetrics = Gauge.build()
            .name("cpu_usage_percent")
            .help("CPU usage percentage")
            .labelNames("service")
            .register();

    private static final Gauge ramMetrics = Gauge.build()
            .name("ram_usage_percent")
            .help("RAM usage in bytes")
            .labelNames("service")
            .register();

    //NETWORK METRICS
    private static final Counter httpRequestCounter = Counter.build()
            .name("http_requests_total")
            .help("Total number of HTTP requests")
            .labelNames("service", "method", "path", "status_code")
            .register();

    private static final Histogram httpRequestDurationHistogram = Histogram.build()
            .name("http_request_duration_seconds")
            .help("Duration of HTTP requests in seconds")
            .labelNames("service", "method", "path", "status_code")
            .register();

    // DATABASE METRICS
    private static final Gauge mongoConnectionPoolSize = Gauge.build()
            .name("mongo_connection_pool_size")
            .help("Size of MongoDB connection pool")
            .labelNames("service")
            .register();

    private static final Gauge mongoActiveConnections = Gauge.build()
            .name("mongo_active_connections")
            .help("Number of active MongoDB connections")
            .labelNames("service")
            .register();

    private static final Gauge mongoAvailableConnections = Gauge.build()
            .name("mongo_available_connections")
            .help("Number of available MongoDB connections")
            .labelNames("service")
            .register();

    private static final Gauge mongoQueryTimeSeconds = Gauge.build()
            .name("mongo_query_time_seconds")
            .help("Execution time for MongoDB queries")
            .labelNames("service")
            .register();

    private static final Gauge mongoMemoryUsageBytes = Gauge.build()
            .name("mongo_memory_usage_bytes")
            .help("MongoDB memory usage in bytes")
            .labelNames("service")
            .register();

    public KafkaMetricsListener() {
        try {
            HTTPServer server = new HTTPServer(8081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * AUTH METRICS
     */
    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"0"}))
    public void listenAuthCpuMetrics(ConsumerRecord<String, String> record) {
        cpuMetrics.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "cpu_usage_percent"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"1"}))
    public void listenAuthRamMetrics(ConsumerRecord<String, String> record) {
        ramMetrics.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "memory_usage_percent"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"2"}))
    public void listenAuthHttpRequestCount(ConsumerRecord<String, String> record) {
        httpRequestCounter.labels("auth-service", "GET", "/", "200").inc(Utils.parseMetrics("auth-service", record.value().split("\n"), "http_requests_total"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"3"}))
    public void listenAuthHttpRequestDuration(ConsumerRecord<String, String> record) {
        httpRequestDurationHistogram.labels("auth-service", "GET", "/", "200").observe(Utils.parseMetrics("auth-service", record.value().split("\n"), "http_request_duration_seconds"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"4"}))
    public void listenAuthMongoConnectionPoolSize(ConsumerRecord<String, String> record) {
        mongoConnectionPoolSize.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "mongo_connection_pool_size"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"5"}))
    public void listenAuthMongoActiveConnections(ConsumerRecord<String, String> record) {
        mongoActiveConnections.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "mongo_active_connections"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"6"}))
    public void listenAuthMongoAvailableConnections(ConsumerRecord<String, String> record) {
        mongoAvailableConnections.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "mongo_available_connections"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"7"}))
    public void listenAuthMongoQueryTimeSeconds(ConsumerRecord<String, String> record) {
        mongoQueryTimeSeconds.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "mongo_query_time_seconds"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "auth-service-topic", partitions = {"8"}))
    public void listenAuthMongoMemoryUsageBytes(ConsumerRecord<String, String> record) {
        mongoMemoryUsageBytes.labels("auth-service").set(Utils.parseMetrics("auth-service", record.value().split("\n"), "mongo_memory_usage_bytes"));
    }

    /**
     * INVENTORY METRICS
     */
    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"0"}))
    public void listenInventoryCpuMetrics(ConsumerRecord<String, String> record) {
        cpuMetrics.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "cpu_usage_percent"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"1"}))
    public void listenInventoryRamMetrics(ConsumerRecord<String, String> record) {
        ramMetrics.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "memory_usage_percent"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"2"}))
    public void listenInventoryHttpRequestCount(ConsumerRecord<String, String> record) {
        httpRequestCounter.labels("inventory-service", "GET", "/", "200").inc(Utils.parseMetrics("inventory-service", record.value().split("\n"), "http_requests_total"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"3"}))
    public void listenInventoryHttpRequestDuration(ConsumerRecord<String, String> record) {
        httpRequestDurationHistogram.labels("inventory-service", "GET", "/", "200").observe(Utils.parseMetrics("inventory-service", record.value().split("\n"), "http_request_duration_seconds"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"4"}))
    public void listenInventoryMongoConnectionPoolSize(ConsumerRecord<String, String> record) {
        mongoConnectionPoolSize.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "mongo_connection_pool_size"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"5"}))
    public void listenInventoryMongoActiveConnections(ConsumerRecord<String, String> record) {
        mongoActiveConnections.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "mongo_active_connections"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"6"}))
    public void listenInventoryMongoAvailableConnections(ConsumerRecord<String, String> record) {
        mongoAvailableConnections.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "mongo_available_connections"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"7"}))
    public void listenInventoryMongoQueryTimeSeconds(ConsumerRecord<String, String> record) {
        mongoQueryTimeSeconds.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "mongo_query_time_seconds"));
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-service-topic", partitions = {"8"}))
    public void listenInventoryMongoMemoryUsageBytes(ConsumerRecord<String, String> record) {
        mongoMemoryUsageBytes.labels("inventory-service").set(Utils.parseMetrics("inventory-service", record.value().split("\n"), "mongo_memory_usage_bytes"));
    }
}
