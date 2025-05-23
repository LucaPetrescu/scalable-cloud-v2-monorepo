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

        if (serviceName.equals("auth-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/auth_rules.yml";
        } else if(serviceName.equals("inventory-service")){
            serviceNameFilePath = "src/main/java/com/masterthesis/alertingsystem/rules/config/inventory_rules.yml";
        }

        try{
            JsonNode metric = queryClient.query(metricQuery);
            ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

            if(!resultArrayNode.isEmpty()){
                JsonNode metricResult = resultArrayNode.get(0);

                String metricName = metricResult.at("/metric/__name__").toString();
                String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                double metricValueDouble = Double.parseDouble(metricValue);

                MetricType metricType = MetricType.fromQueryName(metricQuery);

                processMetricWithThreshold(metricName, metricValueDouble, serviceName);

                metricResponseDto = new MetricResponseDto(metricName, metricValueDouble, metricType.getDisplayName(), metricType.getUnit());

            } else {
                throw new IllegalArgumentException("Metric data is non-existent or invalid");
            }

            return metricResponseDto;

        } catch(Exception e){
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

        String cacheKey = serviceName + ":" + metricName;

        String alertReason = "Metric exceeded for " + serviceName;

        if(thresholdExceeded) {
            if(serviceName.equals("auth-service")){
                redisMessagePublisher.publish(new Message(ServiceType.AUTH_SERVICE, new Alert(alertReason, metricName, metricValue)));
                if(!redisAlertCacheService.isAlertCached(cacheKey)) {
                    redisAlertCacheService.cacheAlert(cacheKey, new Alert(alertReason, metricName, metricValue), 7);
                }
            } else if(serviceName.equals("inventory-service")) {
                redisMessagePublisher.publish(new Message(ServiceType.INVENTORY_SERVICE, new Alert("Metric exceeded for " + serviceName, metricName, metricValue)));
                if(!redisAlertCacheService.isAlertCached(cacheKey)){
                    redisAlertCacheService.cacheAlert(cacheKey, new Alert(alertReason, metricName, metricValue), 7);
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

}
