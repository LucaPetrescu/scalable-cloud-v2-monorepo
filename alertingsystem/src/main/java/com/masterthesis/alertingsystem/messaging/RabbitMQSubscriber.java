package com.masterthesis.alertingsystem.messaging;

import com.masterthesis.alertingsystem.rabbitmq.RabbitMQConfig;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSubscriber {

    @RabbitListener(queues = RabbitMQConfig.ALERTS_QUEUE)
    public void receiveAlert(@Payload Alert alert) {
        try {
            System.out.println("Received alert from RabbitMQ: " + alert);
            // Here you can add any additional processing for received alerts
            // For example, sending to WebSocket clients, updating UI, etc.
        } catch (Exception e) {
            System.err.println("Error processing received alert: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 