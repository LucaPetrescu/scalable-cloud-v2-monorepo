package com.masterthesis.metricscollector.metrics.MongoDBMetrics;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mongodb-metrics")
public class MongoDBMetricsController {

    @PostMapping("/connection-pool-size")
    public String getConnectionPoolSize(@RequestBody String connectionPoolSize){
        System.out.println("Connection Pool Size: " + connectionPoolSize);
        return connectionPoolSize;
    }

    @PostMapping("/active-connections")
    public String getActiveConnections(@RequestBody String activeConnections){
        System.out.println("Active Connections: " + activeConnections);
        return activeConnections;
    }

    @PostMapping("/available-connections")
    public String getAvailableConnections(@RequestBody String availableConnections){
        System.out.println("Available Connections: " + availableConnections);
        return availableConnections;
    }

    @PostMapping("/query-time")
    public String getQueryTime(@RequestBody String queryTime){
        System.out.println("Query Time: " + queryTime);
        return queryTime;
    }

    @PostMapping("/memory-usage")
    public String getMemoryUsage(@RequestBody String memoryUsage){
        System.out.println("Memory Usage: " + memoryUsage);
        return memoryUsage;
    }

}
