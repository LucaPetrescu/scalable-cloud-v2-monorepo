package com.masterthesis.alertingsystem.dtos;

public class MetricResponseDto {

    private String serviceName;
    private String metricName;
    private double metricValue;
    private String metricDisplayName;
    private String metricUnit;

    public MetricResponseDto() {}

    public MetricResponseDto(String serviceName, String metricName, double metricValue, String metricDisplayName, String metricUnit) {
        this.serviceName = serviceName;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.metricDisplayName = metricDisplayName;
        this.metricUnit = metricUnit;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getMetricDisplayName() {
        return metricDisplayName;
    }

    public void setMetricDisplayName(String metricDisplayName) {
        this.metricDisplayName = metricDisplayName;
    }

    public String getUnit() {
        return metricUnit;
    }

    public void setUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

    @Override
    public String toString() {
        return "MetricResponseDto{" +
                ", serviceName='" + serviceName + '\'' +
                ", metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", metricDisplayName='" + metricDisplayName + '\'' +
                ", metricUnit='" + metricUnit + '\'' +
                '}';
    }
}
