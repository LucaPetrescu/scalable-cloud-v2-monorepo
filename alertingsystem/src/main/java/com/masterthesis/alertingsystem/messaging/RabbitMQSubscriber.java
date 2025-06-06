package com.masterthesis.alertingsystem.messaging;

import com.masterthesis.alertingsystem.cache.utils.AlertMessage;
import com.masterthesis.alertingsystem.cache.websocket.WebSocketService;
import com.masterthesis.alertingsystem.rabbitmq.RabbitMQConfig;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSubscriber {

    @Autowired
    private WebSocketService webSocketService;

    @RabbitListener(queues = RabbitMQConfig.ALERTS_QUEUE)
    public void receiveAlert(@Payload AlertMessage alertMessage) {
        try {
            webSocketService.sendAlert(alertMessage, alertMessage.getAlert().getServiceName());
        } catch (Exception e) {
            System.err.println("Error processing received alert: " + e.getMessage());
            e.printStackTrace();
        }
    }
}