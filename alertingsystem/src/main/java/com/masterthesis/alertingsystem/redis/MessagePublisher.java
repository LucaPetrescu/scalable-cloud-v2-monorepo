package com.masterthesis.alertingsystem.redis;

import com.masterthesis.alertingsystem.redis.utils.AlertMessage;

public interface MessagePublisher {
    void publish(AlertMessage message, String topicName);
}
