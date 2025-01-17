package com.masterthesis.alertingsystem.ruleshandling;

import com.fasterxml.jackson.databind.JsonNode;
import com.masterthesis.alertingsystem.query.PrometheusQueryClient;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Iterator;

public class PrometheusAnalyzerService {

    private final PrometheusQueryClient queryClient;
    private final RuleEngine ruleEngine;
    private final ActionHandler actionHandler;

    public PrometheusAnalyzerService(PrometheusQueryClient queryClient, RuleEngine ruleEngine, ActionHandler actionHandler) {
        this.queryClient = queryClient;
        this.ruleEngine = ruleEngine;
        this.actionHandler = actionHandler;
    }

    @Scheduled(fixedRate = 5000)
    public void analyzeMetrics() {
        String[] queries = {"cpu_usage_percent", "ram_usage_percent", "http_requests_total"};

        for (String query : queries) {
            JsonNode metrics = queryClient.query(query);
            Iterator<JsonNode> iterator = metrics.elements();

            while (iterator.hasNext()) {
                JsonNode metricNode = iterator.next();
                String metricName = metricNode.path("metric").path("__name__").asText();
                double value = metricNode.path("value").get(1).asDouble();

                if (ruleEngine.isMetricExceedingThreshold(metricName, value)) {
                    actionHandler.handleThresholdExceeded(metricName, value);
                } else {
                    actionHandler.handleNormalConditions(metricName);
                }
            }
        }
    }

}
