package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.masterthesis.alertingsystem.dtos.NewRuleDto;
import com.masterthesis.alertingsystem.exceptions.ThresholdsLoadingException;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import com.masterthesis.alertingsystem.rules.facts.Metric;
import com.masterthesis.alertingsystem.rules.facts.Threshold;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DroolsRuleEngine {

    private KieContainer kieContainer;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final Path configDir;

    public DroolsRuleEngine(KieContainer kieContainer, Map<String, Map<String, Double>> thresholds) {
        this.kieContainer = kieContainer;
        // Create config directory in user's home
        this.configDir = Paths.get(System.getProperty("user.home"), ".alertingsystem", "config");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            System.err.println("Failed to create config directory: " + e.getMessage());
        }
    }

    private Path getUserConfigPath(String rulesFilePath) {
        String fileName = Paths.get(rulesFilePath).getFileName().toString();
        return configDir.resolve(fileName);
    }

    private JsonNode readConfigFile(String rulesFilePath) throws IOException {
        // First try to read from user's config directory
        Path userConfigPath = getUserConfigPath(rulesFilePath);
        if (Files.exists(userConfigPath)) {
            try (InputStream in = Files.newInputStream(userConfigPath)) {
                return mapper.readTree(in).get("thresholds");
            }
        }

        // If not found in user's directory, read from classpath
        ClassPathResource resource = new ClassPathResource(rulesFilePath);
        try (InputStream in = resource.getInputStream()) {
            return mapper.readTree(in).get("thresholds");
        }
    }

    public List<Threshold> getThresholdsFromYamlFile(String rulesFilePath) throws ThresholdsLoadingException {
        try {
            JsonNode root = readConfigFile(rulesFilePath);
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

        List<Threshold> updatedThresholds = writeNewRulesToYaml(rulesFilePath, newRulesDtoList);
        
        // Reload the KieContainer with new thresholds
        reloadKieContainer();
        
        return updatedThresholds;
    }

    private void reloadKieContainer() {
        try {
            // Dispose of the old container
            if (kieContainer != null) {
                kieContainer.dispose();
            }

            // Create a new KieServices instance
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

            // Load the rules file from classpath
            Resource rulesResource = new ClassPathResource("rules/metrics_rules.drl");
            kieFileSystem.write("src/main/resources/rules/metrics_rules.drl", 
                ResourceFactory.newInputStreamResource(rulesResource.getInputStream()));

            // Build the new container
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            KieModule kieModule = kieBuilder.getKieModule();

            // Create and set the new container
            kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
            
            System.out.println("✅ Successfully reloaded Drools rules engine with new thresholds");
        } catch (Exception e) {
            System.err.println("❌ Failed to reload Drools rules engine: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Threshold> writeNewRulesToYaml(String rulesFilePath, List<NewRuleDto> newRuleDtoList) throws ThresholdsLoadingException {
        mapper.findAndRegisterModules();
        List<Threshold> updatedThresholds = new ArrayList<>();

        try {
            JsonNode root = readConfigFile(rulesFilePath);
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
            }

            if(modified) {
                // Write to the user's config directory
                Path userConfigPath = getUserConfigPath(rulesFilePath);
                mapper.writeValue(userConfigPath.toFile(), new HashMap<String, Object>(Map.of("thresholds", currentThresholds)));
            }

            for(Map.Entry<String, Map<String, Double>> entry : currentThresholds.entrySet()) {
                String metricName = entry.getKey();
                double maxValue = entry.getValue().get("max");
                updatedThresholds.add(new Threshold(metricName, maxValue));
            }

            return updatedThresholds;

        } catch(IOException e) {
            throw new ThresholdsLoadingException("Unable to update current rules with new rules: " + e.getMessage());
        }
    }

    public List<Threshold> getRulesForService(String rulesFilePath) throws ThresholdsLoadingException {

        return getThresholdsFromYamlFile(rulesFilePath);

    }

    public boolean isMetricExceedingThreshold(String metricName, double value, String serviceName) {

        KieSession kieSession = kieContainer.newKieSession();

        try {
            String serviceRulesFilePath;
            if ("auth-service".equals(serviceName)) {
                serviceRulesFilePath = "config/auth_rules.yml";
            } else if ("inventory-service".equals(serviceName)) {
                serviceRulesFilePath = "config/inventory_rules.yml";
            } else {
                System.err.println("❌ Unknown service: " + serviceName);
                return false;
            }

            List<Threshold> thresholds = getThresholdsFromYamlFile(serviceRulesFilePath);

            for (Threshold t : thresholds) {
                kieSession.insert(t);
            }

            Metric metric = new Metric(metricName.replace("\"", ""), value);

            kieSession.insert(metric);

            List<Alert> alerts = new ArrayList<>();
            kieSession.setGlobal("alerts", alerts);
            kieSession.setGlobal("serviceName", serviceName);
            
            kieSession.fireAllRules();
            if (!alerts.isEmpty()) {
                System.err.println("Threshold exceeded for metric " + metricName + " (value=" + value + ") in service " + serviceName + " (alerts=" + alerts + ").");
            }
            return !alerts.isEmpty();

        } catch (ThresholdsLoadingException e) {
            System.err.println("Error loading thresholds: " + e.getMessage());
            return false;
        } finally {
            kieSession.dispose();
        }

    }

}
