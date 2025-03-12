package com.masterthesis.alertingsystem.rules;

import com.masterthesis.alertingsystem.rules.facts.AlertResult;
import com.masterthesis.alertingsystem.rules.facts.MetricData;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DroolsRuleEngine {

    private final KieContainer kieContainer;
    private final Map<String, Map<String, Double>> thresholds;

    public DroolsRuleEngine(KieContainer kieContainer, Map<String, Map<String, Double>> thresholds) {
        this.kieContainer = kieContainer;
        this.thresholds = Map.of(
                "cpu_usage_percent", Map.of("max", 75.0),
                "ram_usage_percent", Map.of("max", 80.0),
                "http_requests_total", Map.of("max", 1000.0));
    }

    public boolean isMetricExceedingThreshold(String metricName, double value) {
        MetricData metricData = new MetricData(metricName, value);
        AlertResult alertResult = new AlertResult();

        KieSession kieSession = kieContainer.newKieSession();
        try{
            kieSession.insert(metricData);
            kieSession.insert(alertResult);
            kieSession.setGlobal("thresholds", thresholds);
            kieSession.fireAllRules();
            return alertResult.isThresholdExceeded();
        }finally {
            kieSession.dispose();
        }

    }

    public Map<String, Double> getThresholdsForMetric(String metricName){
        return thresholds.getOrDefault(metricName, Map.of());
    }
}
