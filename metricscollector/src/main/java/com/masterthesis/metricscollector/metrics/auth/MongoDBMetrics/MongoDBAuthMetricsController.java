package com.masterthesis.metricscollector.metrics.auth.MongoDBMetrics;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mongodb-metrics")
public class MongoDBAuthMetricsController {

    @PostMapping("/connection-pool-size")
    public String getConnectionPoolSize(@RequestBody String connectionPoolSize){
        return connectionPoolSize;
    }

    @PostMapping("/active-connections")
    public String getActiveConnections(@RequestBody String activeConnections){
        return activeConnections;
    }

    @PostMapping("/available-connections")
    public String getAvailableConnections(@RequestBody String availableConnections){
        return availableConnections;
    }

    @PostMapping("/query-time")
    public String getQueryTime(@RequestBody String queryTime){
        return queryTime;
    }

    @PostMapping("/memory-usage")
    public String getMemoryUsage(@RequestBody String memoryUsage){
        return memoryUsage;
    }

}
