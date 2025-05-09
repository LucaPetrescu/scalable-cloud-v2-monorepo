package com.masterthesis.alertingsystem.redis.utils;

import com.masterthesis.alertingsystem.rules.facts.Alert;

public class AlertMessage {

    private String from;
    private String text;
    private String time;

    public AlertMessage(String from, Alert alert, String time) {
        this.from = from;
        this.text = text;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
