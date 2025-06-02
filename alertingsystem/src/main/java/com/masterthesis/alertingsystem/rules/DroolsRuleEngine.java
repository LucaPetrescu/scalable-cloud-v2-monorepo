package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.masterthesis.alertingsystem.dtos.NewRuleDto;
import com.masterthesis.alertingsystem.exceptions.ThresholdsLoadingException;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import com.masterthesis.alertingsystem.rules.facts.Metric;
import com.masterthesis.alertingsystem.rules.facts.Threshold;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DroolsRuleEngine {

    private final KieContainer kieContainer;

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public DroolsRuleEngine(KieContainer kieContainer, Map<String, Map<String, Double>> thresholds) {
        this.kieContainer = kieContainer;
    }

    public List<Threshold> getThresholdsFromYamlFile(String rulesFilePath) throws ThresholdsLoadingException {
        try {
            InputStream in = new FileInputStream(rulesFilePath);
            JsonNode root = mapper.readTree(in).get("thresholds");
            List<Threshold> thresholds = new ArrayList<>();

            root.fieldNames().forEachRemaining(metricName -> {
                double max = root.get(metricName).get("max").asDouble();
                thresholds.add(new Threshold(metricName, max));
            });

            return thresholds;

        } catch(IOException e) {
            throw new ThresholdsLoadingException("Unable to get thresholds: " + e.getMessage());
        }
    }

    public List<Threshold> changeRules(String rulesFilePath, List<NewRuleDto> newRulesDtoList) throws ThresholdsLoadingException {
        List<Threshold> currentRules = getThresholdsFromYamlFile(rulesFilePath);

        Map<String, Double> currentRuleMap = currentRules.stream().collect(Collectors.toMap(Threshold::getName, Threshold::getMax));

        boolean isDifferent = false;

        for (NewRuleDto newRule : newRulesDtoList) {
            Double currentValue = currentRuleMap.get(newRule.getMetricName());

            if (currentValue == null || Double.compare(currentValue, newRule.getValue()) != 0) {
                isDifferent = true;
                break;
            }
        }

        if(!isDifferent) {
            return null;
        }

        return writeNewRulesToYaml(rulesFilePath, newRulesDtoList);

    }

    public List<Threshold> writeNewRulesToYaml(String rulesFilePath, List<NewRuleDto> newRuleDtoList) throws ThresholdsLoadingException {
        mapper.findAndRegisterModules();

        List<Threshold> updatedThresholds = new ArrayList<>();

        try {
            InputStream in = new FileInputStream(rulesFilePath);
            JsonNode root = mapper.readTree(in).get("thresholds");
            Map<String, Map<String, Double>> currentThresholds = new HashMap<>();

            root.fieldNames().forEachRemaining(metricName -> {
                JsonNode values = root.get(metricName);
                Map<String, Double> rule = new HashMap<>();
                values.fields().forEachRemaining(entry -> {
                    rule.put(entry.getKey(), entry.getValue().asDouble());
                });
                currentThresholds.put(metricName, rule);
            });

            boolean modified = false;

            for (NewRuleDto newRuleDto : newRuleDtoList) {
                String metric = newRuleDto.getMetricName();

                Map<String, Double> incoming = new HashMap<>();
                if(newRuleDto.getValue() != 0.0){
                    incoming.put("max", newRuleDto.getValue());
                }

                if(!currentThresholds.containsKey(metric) || !currentThresholds.get(metric).equals(incoming)) {
                    currentThresholds.put(metric, incoming);
                    modified = true;
                }

                if(modified) {
                    mapper.writeValue(new File(rulesFilePath), new HashMap<String, Object>(Map.of("thresholds", currentThresholds)));
                }

            }

            for(Map.Entry<String, Map<String, Double>> entry : currentThresholds.entrySet()) {
                String metricName = entry.getKey();
                double maxValue = entry.getValue().get("max");
                updatedThresholds.add(new Threshold(metricName, maxValue));
            }

            return updatedThresholds;

        } catch(IOException e) {
            throw new ThresholdsLoadingException("Unable to update current rules with new rules " + e.getMessage());
        }
    }

    public List<Threshold> getRulesForService(String rulesFilePath) throws ThresholdsLoadingException {

        return getThresholdsFromYamlFile(rulesFilePath);

    }

    public boolean isMetricExceedingThreshold(String metricName, double value, String serviceRulesFilePath) {

        KieSession kieSession = kieContainer.newKieSession();

        try {
            List<Threshold> thresholds = getThresholdsFromYamlFile(serviceRulesFilePath);

            for (Threshold t : thresholds) {
                kieSession.insert(t);
            }

            Metric metric = new Metric(metricName, value);
            kieSession.insert(metric);

            List<Alert> alerts = new ArrayList<>();
            kieSession.setGlobal("alerts", alerts);

            kieSession.fireAllRules();
            System.out.println(alerts.toString());
            return !alerts.isEmpty();

        } catch (ThresholdsLoadingException e) {
            System.err.println("Error loading thresholds: " + e.getMessage());
            return false;
        } finally {
            kieSession.dispose();
        }

    }

}
