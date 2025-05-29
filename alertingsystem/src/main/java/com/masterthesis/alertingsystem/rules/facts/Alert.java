package com.masterthesis.alertingsystem.rules.facts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Alert {

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("metricName")
    private String affectedMetric;

    @JsonProperty("metricValue")
    private double exceededValue;

    public Alert(String reason, String affectedMetric, double exceededValue) {
        this.reason = reason;
        this.affectedMetric = affectedMetric;
        this.exceededValue = exceededValue;
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
}
