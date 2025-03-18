package com.masterthesis.alertingsystem.dtos;

import java.util.ArrayList;

public class MetricsResponseDto {

    private String metricName;
    private double metricValue;

    public MetricsResponseDto() {}

    public MetricsResponseDto(String metricName, double metricValue) {
        this.metricName = metricName;
        this.metricValue = metricValue;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }
}
