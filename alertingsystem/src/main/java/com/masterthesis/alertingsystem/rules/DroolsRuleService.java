package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.dtos.NewRuleDto;
import com.masterthesis.alertingsystem.dtos.RuleDto;
import com.masterthesis.alertingsystem.exceptions.NothingToUpdateException;
import com.masterthesis.alertingsystem.exceptions.ThresholdsLoadingException;
import com.masterthesis.alertingsystem.query.MetricType;
import com.masterthesis.alertingsystem.query.PrometheusQueryService;
import com.masterthesis.alertingsystem.redis.Message;
import com.masterthesis.alertingsystem.redis.RedisAlertCacheService;
import com.masterthesis.alertingsystem.redis.RedisMessagePublisher;
import com.masterthesis.alertingsystem.redis.utils.AlertMessage;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import com.masterthesis.alertingsystem.rules.facts.Threshold;

import com.masterthesis.alertingsystem.redis.utils.ServiceType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DroolsRuleService {

    @Autowired
    private PrometheusQueryService queryClient;

    @Autowired
    private DroolsRuleEngine droolsRuleEngine;

    @Autowired
    private RedisMessagePublisher redisMessagePublisher;

    @Autowired
    private RedisAlertCacheService redisAlertCacheService;

    public ArrayList<MetricResponseDto> getAllMetrics(String serviceName) {

        ArrayList<MetricResponseDto> queriedMetrics = new ArrayList<>();

        String serviceNameFilePath = "";

        if (serviceName.equals("auth-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/auth_rules.yml";
        } else if(serviceName.equals("inventory-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/inventory_rules.yml";
        }

        for(MetricType metricType: MetricType.values()){

            try{

                double httpRequestDurationSecondsCountValueDouble = getDoubleValueOfMetric(metricType.getQueryName(), "http_request_duration_seconds_count");
                double httpRequestDurationSecondsSumValueDouble = getDoubleValueOfMetric(metricType.getQueryName(), "http_request_duration_seconds_sum");

                double httpRequestDurationSecondsDoubleValue = httpRequestDurationSecondsSumValueDouble / httpRequestDurationSecondsCountValueDouble;

                if(!Double.isNaN(httpRequestDurationSecondsDoubleValue)){
                    queriedMetrics.add(new MetricResponseDto("http_request_duration_seconds", httpRequestDurationSecondsDoubleValue, "HTTP Request Duration", "seconds"));
                }

                JsonNode metric = queryClient.query(metricType.getQueryName());
                ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

                if(!resultArrayNode.isEmpty()) {
                    JsonNode metricResult = resultArrayNode.get(0);

                    String metricName = metricResult.at("/metric/__name__").toString();
                    String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                    double metricValueDouble = Double.parseDouble(metricValue);

                    processMetricWithThreshold(metricName, metricValueDouble, serviceNameFilePath);

                    MetricResponseDto metricsResponse = new MetricResponseDto(metricName, metricValueDouble, metricType.getDisplayName(), metricType.getUnit());
                    queriedMetrics.add(metricsResponse);
                } else {
                    throw new IllegalArgumentException("Metric data is non-existent or invalid");
                }
            } catch (Exception e){
                System.err.println("❌ Metric retrieval failed: " + e.getMessage());
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
            // Special case: compute average duration
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

                        metricResponseDto = new MetricResponseDto(
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
                // Regular single-metric query
                JsonNode metric = queryClient.query(metricQuery);
                ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

                if (!resultArrayNode.isEmpty()) {
                    JsonNode metricResult = resultArrayNode.get(0);

                    String metricName = metricResult.at("/metric/__name__").toString();
                    String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                    double metricValueDouble = Double.parseDouble(metricValue);

                    MetricType metricType = MetricType.fromQueryName(metricQuery);

                    processMetricWithThreshold(metricName, metricValueDouble, serviceName);

                    metricResponseDto = new MetricResponseDto(
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

        String cacheKey = serviceName + ":" + metricName + "-" + metricValue;

        String alertReason = "Metric exceeded for " + serviceName;

        Alert dummyAlert = new Alert("dummy", "dummy", 0);

        AlertMessage alertMessage = new AlertMessage(dummyAlert, "time");

        redisMessagePublisher.publish(alertMessage, "auth-service-alerts-topic");

        if(thresholdExceeded) {
            if(serviceName.equals("auth-service")){
                if(!redisAlertCacheService.isAlertCached(cacheKey)) {

                    Alert alert = new Alert(alertReason, metricName, metricValue);

                    redisAlertCacheService.cacheAlert(cacheKey, alert, 7);
                    redisMessagePublisher.publish(alertMessage, "auth-service-alerts-topic");
//                    redisMessagePublisher.publish(new Message(ServiceType.AUTH_SERVICE, alert), "auth-service-alerts-topic");
                } else {
                    Alert alert = redisAlertCacheService.getCachedAlert(alertReason);
//                    redisMessagePublisher.publish(new Message(ServiceType.AUTH_SERVICE, alert), "auth-service-alerts-topic");
                }
            } else if(serviceName.equals("inventory-service")) {
                if(!redisAlertCacheService.isAlertCached(cacheKey)){

                    Alert alert = new Alert(alertReason, metricName, metricValue);

                    redisAlertCacheService.cacheAlert(cacheKey, new Alert(alertReason, metricName, metricValue), 7);
//                    redisMessagePublisher.publish(new Message(ServiceType.INVENTORY_SERVICE, alert), "inventory-service-alerts-topic");
                } else {
                    Alert alert = redisAlertCacheService.getCachedAlert(alertReason);
//                    redisMessagePublisher.publish(new Message(ServiceType.INVENTORY_SERVICE, alert));
                }
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

}
