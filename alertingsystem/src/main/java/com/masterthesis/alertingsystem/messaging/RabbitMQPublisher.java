package com.masterthesis.alertingsystem.messaging;

import com.masterthesis.alertingsystem.rabbitmq.RabbitMQConfig;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAlert(Alert alert) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERTS_EXCHANGE,
                RabbitMQConfig.ALERTS_ROUTING_KEY,
                alert
            );
            System.out.println("Published alert to RabbitMQ: " + alert);
        } catch (Exception e) {
            System.err.println("Error publishing alert to RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 