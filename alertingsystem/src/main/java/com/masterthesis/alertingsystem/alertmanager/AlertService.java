package com.masterthesis.alertingsystem.alertmanager;

import com.masterthesis.alertingsystem.redis.MessagePublisher;

public class AlertService {

    private final MessagePublisher messagePublisher;

    public AlertService(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public void sendAlert(String message) {
        messagePublisher.publish(message);
    }
}
