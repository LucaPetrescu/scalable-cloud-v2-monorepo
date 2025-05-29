package com.masterthesis.alertingsystem.redis.utils;

import com.masterthesis.alertingsystem.rules.facts.Alert;

public class AlertNotification {

    private final Alert alert;
    private final String serviceName;

    private final String timestamp;

    public AlertNotification(Alert alert, String serviceName, String timestamp) {
        this.alert = alert;
        this.serviceName = serviceName;
        this.timestamp = timestamp;
    }

    public Alert getAlert() {
        return alert;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
