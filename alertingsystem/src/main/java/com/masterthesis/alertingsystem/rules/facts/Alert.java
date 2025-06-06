package com.masterthesis.alertingsystem.rules.facts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("metricName")
    private String affectedMetric;

    @JsonProperty("metricValue")
    private double exceededValue;

    // Default constructor required for serialization
    public Alert() {
    }

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

    @Override
    public String toString() {
        return "Alert{" +
                "reason='" + reason + '\'' +
                ", affectedMetric='" + affectedMetric + '\'' +
                ", exceededValue=" + exceededValue +
                '}';
    }
}
