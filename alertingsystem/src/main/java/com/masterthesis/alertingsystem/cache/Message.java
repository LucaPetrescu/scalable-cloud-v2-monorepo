package com.masterthesis.alertingsystem.cache;

import com.masterthesis.alertingsystem.cache.utils.ServiceType;
import com.masterthesis.alertingsystem.rules.facts.Alert;

public class Message {

    private ServiceType serviceType;
    private Alert alert;

    public Message(ServiceType serviceType, Alert alert) {
        this.serviceType = serviceType;
        this.alert = alert;
    }

    public String getServiceName() {
        return serviceType.getServiceName();
    }

    public String getTopicName() {
        return serviceType.getTopicName();
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}
