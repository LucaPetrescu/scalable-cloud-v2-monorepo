package com.masterthesis.alertingsystem.rules.facts;

public class Threshold {

    private String name;
    private double max;

    public Threshold(String name, double max) {
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
