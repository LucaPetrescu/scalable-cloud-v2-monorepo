package com.masterthesis.alertingsystem.rules.facts;

public class AlertResult {

    private boolean thresholdExceeded = false;
    private String reason;

    public boolean isThresholdExceeded() {
        return thresholdExceeded;
    }

    public void setThresholdExceeded(boolean thresholdExceeded) {
        this.thresholdExceeded = thresholdExceeded;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
