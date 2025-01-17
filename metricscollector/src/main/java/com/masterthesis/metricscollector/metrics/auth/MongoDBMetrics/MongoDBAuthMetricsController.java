package com.masterthesis.metricscollector.metrics.auth.MongoDBMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mongodb-metrics")
public class MongoDBAuthMetricsController {

    private KafkaTemplate<String, String> kafkaTemplate;

    public MongoDBAuthMetricsController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    private String topicName = "auth-service-topic";

    @PostMapping("/connection-pool-size")
    public String getConnectionPoolSize(@RequestBody String connectionPoolSize) {

        if(connectionPoolSize.isEmpty()){
            throw new MetricReceivingException("[getConnectionPoolSize] Metric not received");
        }

        kafkaTemplate.send(topicName, 4, "connection_pool_size", connectionPoolSize);

        return connectionPoolSize;
    }

    @PostMapping("/active-connections")
    public String getActiveConnections(@RequestBody String activeConnections) {

        if(activeConnections.isEmpty()){
            throw new MetricReceivingException("[getActiveConnections] Metric not received");
        }

        kafkaTemplate.send(topicName, 5, "active_connections", activeConnections);

        return activeConnections;
    }

    @PostMapping("/available-connections")
    public String getAvailableConnections(@RequestBody String availableConnections) {

        if(availableConnections.isEmpty()) {
            throw new MetricReceivingException("[getAvailableConnections] Metric not received");
        }

        kafkaTemplate.send(topicName, 6, "available_connections", availableConnections);

        return availableConnections;
    }

    @PostMapping("/query-time")
    public String getQueryTime(@RequestBody String queryTime) {

        if(queryTime.isEmpty()) {
            throw new MetricReceivingException("[getQueryTime] Metric not received");
        }

        kafkaTemplate.send(topicName, 7, "query_time", queryTime);

        return queryTime;
    }

    @PostMapping("/memory-usage")
    public String getMemoryUsage(@RequestBody String memoryUsage) {

        if(memoryUsage.isEmpty()) {
            throw new MetricReceivingException("[getMemoryUsage] Metric not received");
        }

        kafkaTemplate.send(topicName, 8, "memory_usage", memoryUsage);

        return memoryUsage;
    }

}
