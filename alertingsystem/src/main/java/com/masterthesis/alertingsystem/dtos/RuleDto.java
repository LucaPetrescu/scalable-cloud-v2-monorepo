package com.masterthesis.alertingsystem.dtos;

public class RuleDto {

    private String name;
    private double max;

    public RuleDto(String name, double max) {
        this.name = name;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
