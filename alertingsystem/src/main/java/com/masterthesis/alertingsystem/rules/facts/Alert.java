package com.masterthesis.alertingsystem.rules.facts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("metricName")
    private String affectedMetric;

    @JsonProperty("metricValue")
    private double exceededValue;

    public Alert() {
    }

    public Alert(String serviceName, String reason, String affectedMetric, double exceededValue) {
        this.serviceName = serviceName;
        this.reason = reason;
        this.affectedMetric = affectedMetric;
        this.exceededValue = exceededValue;
    }

    public String getServiceName() { return serviceName; }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAffectedMetric() {
        return affectedMetric;
    }

    public void setAffectedMetric(String affectedMetric) {
        this.affectedMetric = affectedMetric;
    }

    public double getExceededValue() {
        return exceededValue;
    }

    public void setExceededValue(double exceededValue) {
        this.exceededValue = exceededValue;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "serviceName='" + serviceName + '\'' +
                ", reason='" + reason + '\'' +
                ", affectedMetric='" + affectedMetric + '\'' +
                ", exceededValue=" + exceededValue +
                '}';
    }
}
