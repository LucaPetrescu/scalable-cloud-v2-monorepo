package com.masterthesis.alertingsystem.ruleshandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public class RuleEngine {

    private final Map<String, Map<String, Double>> thresholds;

    public RuleEngine(Map<String, Map<String, Double>> thresholds) {
        this.thresholds = thresholds;
    }

    private Map<String, Map<String, Double>> loadThresholds() {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("rules.yml")){
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, Map.class);
        }catch(Exception e){
            throw new RuntimeException("Error loading thresholds from rules.yml", e);
        }
    }

    public boolean isMetricExceedingThreshold(String metricName, double value) {
        if (!thresholds.containsKey(metricName)) {
            return false;
        }

        Map<String, Double> metricThresholds = thresholds.get(metricName);
        Double max = metricThresholds.get("max");
        Double min = metricThresholds.get("min");

        return (max != null && value > max) || (min != null && value < min);
    }

    public Map<String, Double> getThresholdsForMetric(String metricName){
        return thresholds.getOrDefault(metricName, Map.of());
    }
}
