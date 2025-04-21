package com.masterthesis.alertingsystem.dtos;

public class NewRuleDto {

    private String metricName;
    private double value;

    public NewRuleDto(String metricName, double value) {
        this.metricName = metricName;
        this.value = value;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
