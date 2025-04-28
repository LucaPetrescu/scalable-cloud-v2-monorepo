package com.masterthesis.alertingsystem.redis.utils;

public enum ServiceType {

    AUTH_SERVICE("auth-service", "auth-service-alerts-topic"),

    INVENTORY_SERVICE("inventory-service", "inventory-service-alerts-topic");

    private final String serviceName;

    private final String topicName;
    ServiceType(String serviceName, String topicName) {
        this.serviceName = serviceName;
        this.topicName = topicName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTopicName() {
        return topicName;
    }
}

