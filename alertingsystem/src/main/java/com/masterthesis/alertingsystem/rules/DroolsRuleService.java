package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.masterthesis.alertingsystem.query.PrometheusQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class DroolsRuleService {

    @Autowired
    private PrometheusQueryService queryClient;
    private DroolsRuleEngine droolsRuleEngine;

    @Scheduled(fixedRate = 5000)
    public void analyzeMetrics() {
        String[] queries = {
                "cpu_usage_percent",
                "ram_usage_percent",
                "http_requests_total",
                "http_request_duration_seconds",
                "mongo_connection_pool_size",
                "mongo_mongo_active_connections",
                "mongo_available_connections",
                "mongo_query_time_seconds",
                "mongo_memory_usage_bytes"
            };

        for(String query : queries) {
            JsonNode metrics = queryClient.query(query);
            Iterator<JsonNode> iterator = metrics.elements();

            while(iterator.hasNext()){
                JsonNode metricNode = iterator.next();
                System.out.println("System: " + metricNode);
            }
        }
    }

    public void analyzeCustomMetric(String metricQueryName) {
        JsonNode metrics = queryClient.query(metricQueryName);
        Iterator<JsonNode> iterator = metrics.elements();

        while(iterator.hasNext()){
            JsonNode metricNode = iterator.next();
            String metricName = metricNode.path("metric").path("__name__").asText();
            double metricValue = metricNode.path("value").get(1).asDouble();

//            if(ruleEngine.isMetricExceedingThreshold(metricName, metricValue)){
//                handleThresholdExceeded(metricName, metricValue);
//            }
        }
    }

}
