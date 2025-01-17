package com.masterthesis.alertingsystem.ruleshandling;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RuleEngine {

    private final Map<String, Map<String, Double>> thresholds;

    public RuleEngine(Map<String, Map<String, Double>> thresholds) {
        this.thresholds = thresholds;
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
}
