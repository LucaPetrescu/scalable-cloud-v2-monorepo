package com.masterthesis.alertingsystem.redis;

public interface MessagePublisher {

    void publish(String message);

}
