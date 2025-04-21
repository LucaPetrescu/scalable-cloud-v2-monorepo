package com.masterthesis.alertingsystem.redis;

public interface MessagePublisher {

    void publish(Message message);

}
