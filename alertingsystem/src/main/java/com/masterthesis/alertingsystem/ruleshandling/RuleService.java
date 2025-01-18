package com.masterthesis.alertingsystem.ruleshandling;

import com.fasterxml.jackson.databind.JsonNode;
import com.masterthesis.alertingsystem.query.PrometheusQueryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class RuleService {

    @Autowired
    private PrometheusQueryClient queryClient;
    private RuleEngine ruleEngine;

    @Scheduled(fixedRate = 5000)
    public void analyzeMetrics() {
        String[] queries = {"cpu_usage_percent", "memory_usage_percent", "http_requests_total"};

        for(String query : queries) {
            JsonNode metrics = queryClient.query(query);
            Iterator<JsonNode> iterator = metrics.elements();

            while(iterator.hasNext()){
                JsonNode metricNode = iterator.next();
//                String metricName = metricNode.path("metric").path("__name__").asText();
//                double metricValue = metricNode.path("value").get(1).asDouble();
                System.out.println("System: " + metricNode);

//                if(ruleEngine.isMetricExceedingThreshold(metricName, metricValue)){
//                    handleThresholdExceeded(metricName, metricValue);
//                }
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

            if(ruleEngine.isMetricExceedingThreshold(metricName, metricValue)){
                handleThresholdExceeded(metricName, metricValue);
            }
        }
    }

    private void handleThresholdExceeded(String metricName, double value) {

        System.out.println("Threshold exceeded for metric " + metricName + " with value " + value);

        switch (metricName) {
            case "cpu_usage_percent":
                handleCpuOverload(value);
                break;
            case "memory_usage_percent":
                handleRamOverload(value);
                break;
            case "http_requests_total":
                handleHighHttpTraffic(value);
                break;
            default:
                System.out.println("No specific action defined for metric " + metricName);
        }
    }

    private void handleCpuOverload(double value){
        System.out.println("Handling high CPU overload");
        scaleUpCpuResources();
        assignLightweightAlgorithm();
        recommendUserAction("Please consider usage of the CPU");
    }

    private void handleRamOverload(double value){
        System.out.println("Handling high RAM overload");
        clearCache();
    }

    private void handleHighHttpTraffic(double value){
        System.out.println("Handling high HTTP traffic");
        scaleOutWebServers();
        enableLoadBalancing();
    }

    private void scaleUpCpuResources() {
        System.out.println("Scaling up resources for CPU...");
    }

    private void clearCache() {
        System.out.println("Clearing cache...");
    }

    private void scaleOutWebServers() {
        System.out.println("Scaling out web servers...");
    }

    private void assignLightweightAlgorithm() {
        System.out.println("Assigning lightweight task allocation algorithm...");
    }

    private void enableLoadBalancing() {
        System.out.println("Enabling load balancing algorithm...");
    }

    private void recommendUserAction(String recommendation) {
        System.out.println("Recommendation for user: " + recommendation);
    }

}
