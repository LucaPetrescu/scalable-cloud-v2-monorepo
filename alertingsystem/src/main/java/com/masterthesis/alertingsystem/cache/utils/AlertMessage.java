package com.masterthesis.alertingsystem.cache.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.masterthesis.alertingsystem.rules.facts.Alert;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class AlertMessage {

    @JsonProperty("alert")
    private Alert alert;

    @JsonProperty("time")
    private String time;

    public AlertMessage(Alert alert, String time) {
        this.alert = alert;
        this.time = time;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
