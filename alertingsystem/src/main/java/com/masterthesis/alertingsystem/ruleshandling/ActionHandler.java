package com.masterthesis.alertingsystem.ruleshandling;

import org.springframework.stereotype.Service;

@Service
public class ActionHandler {

    public void handleThresholdExceeded(String metricName, double value) {
        System.out.println("Threshold exceeded for metric " + metricName + " with value " + value);

        switch (metricName) {
            case "cpu_usage_percent":
                handleCpuOverload(value);
                break;
            case "ram_usage_percent":
                handleRamOverload(value);
                break;
            case "http_requests_total":
                handleHighHttpTraffic(value);
                break;
            default:
                System.out.println("No specific action defined for metric " + metricName);
        }
    }

    public void handleNormalConditions(String metricName) {
        System.out.println("Metric " + metricName + " is within normal conditions.");
        // Log or reset resource allocation for normal conditions
    }

    private void handleCpuOverload(double value) {
        System.out.println("CPU overload detected with value: " + value);

        // **Resource Allocation Logic**: Scale up CPU resources
        scaleUpCpuResources();

        // **Algorithm Assignment Logic**: Switch to a lightweight task allocation algorithm
        assignLightweightAlgorithm();

        // **Recommendation**: Notify user
        recommendUserAction("Consider optimizing CPU-intensive tasks or upgrading the CPU capacity.");
    }

    private void handleRamOverload(double value) {
        System.out.println("RAM overload detected with value: " + value);

        // **Resource Allocation Logic**: Free up memory by clearing cache
        clearMemoryCache();

        // **Recommendation**: Notify user
        recommendUserAction("Consider optimizing applications using large memory or increasing RAM capacity.");
    }

    private void handleHighHttpTraffic(double value) {
        System.out.println("High HTTP traffic detected with value: " + value);

        // **Resource Allocation Logic**: Scale out web servers
        scaleOutWebServers();

        // **Algorithm Assignment Logic**: Enable load balancing algorithm
        enableLoadBalancing();

        // **Recommendation**: Notify user
        recommendUserAction("Consider adding more instances or implementing rate-limiting for heavy traffic.");
    }

    private void scaleUpCpuResources() {
        System.out.println("Scaling up CPU resources...");
        // Logic to provision additional CPU resources
    }

    private void clearMemoryCache() {
        System.out.println("Clearing memory cache...");
        // Logic to free up memory, e.g., clear caches
    }

    private void scaleOutWebServers() {
        System.out.println("Scaling out web servers...");
        // Logic to scale out servers (e.g., deploying additional instances)
    }

    private void assignLightweightAlgorithm() {
        System.out.println("Assigning lightweight task allocation algorithm...");
        // Logic to switch task allocation algorithm
    }

    private void enableLoadBalancing() {
        System.out.println("Enabling load balancing algorithm...");
        // Logic to enable load balancing
    }

    private void recommendUserAction(String recommendation) {
        System.out.println("Recommendation for user: " + recommendation);
        // Notify or log the recommendation for user visibility
    }

}
