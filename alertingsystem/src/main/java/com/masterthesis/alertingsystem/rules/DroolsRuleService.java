package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.masterthesis.alertingsystem.cache.CacheService;
import com.masterthesis.alertingsystem.cache.utils.AlertMessage;
import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.dtos.NewRuleDto;
import com.masterthesis.alertingsystem.dtos.RuleDto;
import com.masterthesis.alertingsystem.exceptions.NothingToUpdateException;
import com.masterthesis.alertingsystem.exceptions.ThresholdsLoadingException;
import com.masterthesis.alertingsystem.query.MetricType;
import com.masterthesis.alertingsystem.query.PrometheusQueryService;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import com.masterthesis.alertingsystem.rules.facts.Threshold;
import com.masterthesis.alertingsystem.messaging.RabbitMQPublisher;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class DroolsRuleService {

    @Autowired
    private PrometheusQueryService queryClient;

    @Autowired
    private DroolsRuleEngine droolsRuleEngine;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RabbitMQPublisher rabbitMQPublisher;

    private final Random random = new Random();
    
    // Store the latest dummy metrics
    private MetricResponseDto latestAuthHttpMetrics = null;
    private MetricResponseDto latestAuthDurationMetrics = null;
    private MetricResponseDto latestInventoryHttpMetrics = null;
    private MetricResponseDto latestInventoryDurationMetrics = null;
    private long lastDummyMetricsUpdate = 0;

    @Scheduled(fixedRate = 60000) // Runs every 3 seconds
    public void generateDummyRequestDurationMetrics() {
        try {
            // Generate different random durations between 1.1 and 5 seconds for each service
            double authDuration = 1.1 + (random.nextDouble() * 3.9); // Random between 1.1 and 5.0
            double inventoryDuration = 1.1 + (random.nextDouble() * 3.9); // Different random between 1.1 and 5.0
            
            // Generate different request counts for each service
            int authRequestCount = 5 + random.nextInt(6); // Random between 5 and 10
            int inventoryRequestCount = 5 + random.nextInt(6); // Different random between 5 and 10
            
            // Calculate sums based on different durations and counts
            double authSum = authDuration * authRequestCount;
            double inventorySum = inventoryDuration * inventoryRequestCount;
            
            // Calculate average durations
            double authAvgDuration = authSum / authRequestCount;
            double inventoryAvgDuration = inventorySum / inventoryRequestCount;
            
            // Store the latest dummy metrics
            latestAuthHttpMetrics = new MetricResponseDto(
                "auth-service",
                "\"http_requests_total\"",
                authRequestCount,
                "HTTP Requests Total",
                "requests"
            );
            latestAuthDurationMetrics = new MetricResponseDto(
                "auth-service",
                "http_request_duration_seconds",
                authAvgDuration,
                "HTTP Request Duration",
                "seconds"
            );
            latestInventoryHttpMetrics = new MetricResponseDto(
                "inventory-service",
                "\"http_requests_total\"",
                inventoryRequestCount,
                "HTTP Requests Total",
                "requests"
            );
            latestInventoryDurationMetrics = new MetricResponseDto(
                "inventory-service",
                "http_request_duration_seconds",
                inventoryAvgDuration,
                "HTTP Request Duration",
                "seconds"
            );
            
            lastDummyMetricsUpdate = System.currentTimeMillis();
            
            // Process metrics with thresholds
            processMetricWithThreshold("http_request_duration_seconds", authAvgDuration, "auth-service");
            processMetricWithThreshold("http_request_duration_seconds", inventoryAvgDuration, "inventory-service");
            processMetricWithThreshold("http_requests_total", authRequestCount, "auth-service");
            processMetricWithThreshold("http_requests_total", inventoryRequestCount, "inventory-service");
            
            System.out.println("Generated dummy metrics - Auth: avg=" + authAvgDuration + 
                             "s (sum=" + authSum + "s, count=" + authRequestCount + 
                             "), Inventory: avg=" + inventoryAvgDuration + 
                             "s (sum=" + inventorySum + "s, count=" + inventoryRequestCount + ")");
        } catch (Exception e) {
            System.err.println("❌ Failed to generate dummy metrics: " + e.getMessage());
        }
    }

    public ArrayList<MetricResponseDto> getAllMetrics(String serviceName) {
        ArrayList<MetricResponseDto> queriedMetrics = new ArrayList<>();

        String serviceNameFilePath = "";

        if (serviceName.equals("auth-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/auth_rules.yml";
        } else if(serviceName.equals("inventory-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/inventory_rules.yml";
        }

        for(MetricType metricType: MetricType.values()){
            if (metricType == MetricType.HTTP_DURATION ||
                metricType == MetricType.HTTP_REQUEST_DURATION_SECONDS_SUM || 
                metricType == MetricType.HTTP_REQUEST_DURATION_SECONDS_COUNT ||
                metricType == MetricType.HTTP_REQUESTS) {
                continue;
            }

            try{
                JsonNode metric = queryClient.query(metricType.getQueryName());
                ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

                if(!resultArrayNode.isEmpty()) {
                    // Find the metric for the requested service
                    for (JsonNode metricResult : resultArrayNode) {
                        String metricServiceName = metricResult.at("/metric/service").asText();
                        if (metricServiceName.equals(serviceName)) {
                            String metricName = metricResult.at("/metric/__name__").toString();
                            String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                            double metricValueDouble = Double.parseDouble(metricValue);

                            processMetricWithThreshold(metricName, metricValueDouble, serviceName);
                            MetricResponseDto metricsResponse = new MetricResponseDto(
                                serviceName,
                                metricName,
                                metricValueDouble,
                                metricType.getDisplayName(),
                                metricType.getUnit()
                            );
                            queriedMetrics.add(metricsResponse);
                            break;
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Metric data is non-existent or invalid");
                }
            } catch (Exception e){
                System.err.println("❌ Metric retrieval failed: " + e.getMessage());
            }
        }

        // Add dummy HTTP metrics if they were updated in the last 60 seconds
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDummyMetricsUpdate < 60000) {
            if (serviceName.equals("auth-service") && latestAuthHttpMetrics != null && latestAuthDurationMetrics != null) {
                queriedMetrics.add(latestAuthHttpMetrics);
                queriedMetrics.add(latestAuthDurationMetrics);
            } else if (serviceName.equals("inventory-service") && latestInventoryHttpMetrics != null && latestInventoryDurationMetrics != null) {
                queriedMetrics.add(latestInventoryHttpMetrics);
                queriedMetrics.add(latestInventoryDurationMetrics);
            }
        }

        return queriedMetrics;
    }

    public MetricResponseDto getMetric(String serviceName, String metricQuery) {

        MetricResponseDto metricResponseDto = null;

        String serviceNameFilePath = "";

        if (serviceName.equals("auth-service")) {
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/auth_rules.yml";
        } else if (serviceName.equals("inventory-service")) {
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/inventory_rules.yml";
        }

        try {

            if ("http_request_duration_seconds".equals(metricQuery)) {

                JsonNode sumMetric = queryClient.query("http_request_duration_seconds_sum");
                JsonNode countMetric = queryClient.query("http_request_duration_seconds_count");

                ArrayNode sumResult = (ArrayNode) sumMetric.at("/data/result");
                ArrayNode countResult = (ArrayNode) countMetric.at("/data/result");

                if (!sumResult.isEmpty() && !countResult.isEmpty()) {
                    double sumValue = Double.parseDouble(sumResult.get(0).at("/value").get(1).asText());
                    double countValue = Double.parseDouble(countResult.get(0).at("/value").get(1).asText());

                    if (countValue > 0) {
                        double avgDuration = sumValue / countValue;
                        processMetricWithThreshold("http_request_duration_seconds_avg", avgDuration, serviceName);

                        metricResponseDto = new MetricResponseDto(serviceName,
                                "http_request_duration_seconds",
                                avgDuration,
                                "HTTP Request Duration",
                                "seconds"
                        );
                    } else {
                        throw new IllegalStateException("Metric count is zero, cannot compute average.");
                    }

                } else {
                    throw new IllegalArgumentException("Sum or Count metric is missing.");
                }

            } else {
                JsonNode metric = queryClient.query(metricQuery);
                ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

                if (!resultArrayNode.isEmpty()) {
                    JsonNode metricResult = resultArrayNode.get(0);

                    String metricName = metricResult.at("/metric/__name__").toString();
                    String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                    double metricValueDouble = Double.parseDouble(metricValue);

                    MetricType metricType = MetricType.fromQueryName(metricQuery);

                    processMetricWithThreshold(metricName, metricValueDouble, serviceName);

                    metricResponseDto = new MetricResponseDto(serviceName,
                            metricName,
                            metricValueDouble,
                            metricType.getDisplayName(),
                            metricType.getUnit()
                    );
                } else {
                    throw new IllegalArgumentException("Metric data is non-existent or invalid");
                }
            }

            return metricResponseDto;

        } catch (Exception e) {
            System.err.println("❌ Metric retrieval failed: " + e.getMessage());
        }

        return metricResponseDto;
    }


    public List<NewRuleDto> changeMetricsRules(String serviceName, List<NewRuleDto> newRulesDtoList) throws ThresholdsLoadingException, NothingToUpdateException {

        String serviceNameFilePath = "";

        List<NewRuleDto> newRules = new ArrayList<>();

        if (serviceName.equals("auth-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/auth_rules.yml";
        } else if(serviceName.equals("inventory-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/inventory_rules.yml";
        }

        List<Threshold> updatedRules = droolsRuleEngine.changeRules(serviceNameFilePath, newRulesDtoList);

        if(updatedRules == null){
            throw new NothingToUpdateException("Nothing new to update");
        }

        for(Threshold threshold : updatedRules) {
            newRules.add(new NewRuleDto(threshold.getName(), threshold.getMax()));
        }

        return newRules;

    }

    public void processMetricWithThreshold(String metricName, double metricValue, String serviceName) {
        
        boolean thresholdExceeded = droolsRuleEngine.isMetricExceedingThreshold(metricName, metricValue, serviceName);

        String cacheKey = serviceName + ":" + metricName.replace("\"", "") + "-" + metricValue;
        String alertReason = "Metric exceeded for " + serviceName;
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if(thresholdExceeded) {
            try {
                if(cacheService.getFromCache(cacheKey) == null) {

                    Alert alert = new Alert(serviceName, alertReason, metricName.replace("\"", ""), metricValue);
                    cacheService.saveToCache(cacheKey, alert);

                    AlertMessage alertMessage = new AlertMessage(alert, currentTime);
                    rabbitMQPublisher.publishAlert(alertMessage);

                } else {
                    Alert alert = (Alert) cacheService.getFromCache(cacheKey);
                    AlertMessage alertMessage = new AlertMessage(alert, currentTime);
                    rabbitMQPublisher.publishAlert(alertMessage);
                }
            } catch (Exception e) {
                System.err.println("Error handling threshold alert: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public List<RuleDto> getRulesForService(String serviceName) throws ThresholdsLoadingException {

        List<RuleDto> rulesDtosList = new ArrayList<>();

        String serviceNameFilePath = "";

        if (serviceName.equals("auth-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/auth_rules.yml";
        } else if(serviceName.equals("inventory-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/inventory_rules.yml";
        }

        List<Threshold> rulesForService = droolsRuleEngine.getRulesForService(serviceNameFilePath);

        for(Threshold threshold : rulesForService) {
            rulesDtosList.add(new RuleDto(threshold.getName(), threshold.getMax()));
        }

        return rulesDtosList;

    }

    private double getDoubleValueOfMetric(String metricName, String metricToCompare) {

        double doubleValue = 0;

        if(metricName.equals(metricToCompare)){

            JsonNode metric = queryClient.query(metricName);
            ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

            if(!resultArrayNode.isEmpty()) {
                JsonNode metricResult = resultArrayNode.get(0);

                String metricNameString = metricResult.at("/metric/__name__").toString();
                String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                double metricValueDouble = Double.parseDouble(metricValue);

                return metricValueDouble;

            }
        }

        return doubleValue;

    }

    public MetricResponseDto getRequestDurationMetrics(String serviceName) {
        try {
            JsonNode sumMetric = queryClient.query("http_request_duration_seconds_sum");
            ArrayNode sumResult = (ArrayNode) sumMetric.at("/data/result");

            JsonNode countMetric = queryClient.query("http_request_duration_seconds_count");
            ArrayNode countResult = (ArrayNode) countMetric.at("/data/result");

            if (!sumResult.isEmpty() && !countResult.isEmpty()) {
                JsonNode serviceSumMetric = null;
                JsonNode serviceCountMetric = null;
                
                for (JsonNode node : sumResult) {
                    String service = node.at("/metric/service").asText();
                    if (service.equals(serviceName)) {
                        serviceSumMetric = node;
                        break;
                    }
                }
                
                for (JsonNode node : countResult) {
                    String service = node.at("/metric/service").asText();
                    if (service.equals(serviceName)) {
                        serviceCountMetric = node;
                        break;
                    }
                }

                if (serviceSumMetric != null && serviceCountMetric != null) {
                    double sumValue = Double.parseDouble(serviceSumMetric.at("/value").get(1).asText());
                    double countValue = Double.parseDouble(serviceCountMetric.at("/value").get(1).asText());
                    double avgDuration = countValue > 0 ? sumValue / countValue : 0;

                    return new MetricResponseDto(
                        serviceName,
                        "http_request_duration_seconds",
                        avgDuration,
                        "HTTP Request Duration Average",
                        "seconds"
                    );
                }
            }
            throw new IllegalArgumentException("Could not find metrics for service: " + serviceName);
        } catch (Exception e) {
            System.err.println("❌ Request duration metrics retrieval failed: " + e.getMessage());
            return null;
        }
    }

}
