package com.masterthesis.alertingsystem.redis;

import com.masterthesis.alertingsystem.rules.facts.Alert;

public class Message {

    private String serviceName;
    private String topicName;
    private Alert alert;

    public Message(String serviceName, String topicName, Alert alert) {
        this.serviceName = serviceName;
        this.topicName = topicName;
        this.alert = alert;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}
