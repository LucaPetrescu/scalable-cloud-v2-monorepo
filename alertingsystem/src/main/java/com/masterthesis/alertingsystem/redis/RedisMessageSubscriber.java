package com.masterthesis.alertingsystem.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterthesis.alertingsystem.redis.utils.AlertMessage;
import com.masterthesis.alertingsystem.redis.websocket.WebSocketService;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public RedisMessageSubscriber(ObjectMapper objectMapper, WebSocketService webSocketService) {
        this.objectMapper = objectMapper;
        this.webSocketService = webSocketService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String topic = new String(pattern);
            String serviceName = topic.contains("auth-service") ? "auth-service" : "inventory-service";
            String messageBody = new String(message.getBody());
            
            System.out.println("Received message: " + messageBody);
            
            AlertMessage alertMessage = objectMapper.readValue(messageBody, AlertMessage.class);
            if (alertMessage == null || alertMessage.getAlert() == null) {
                System.err.println("Failed to deserialize message or alert is null");
                return;
            }
            
            Alert alert = alertMessage.getAlert();
            webSocketService.sendAlert(alert, serviceName);

        } catch (Exception e) {
            System.err.println("Error processing Redis message: " + e.getMessage());
            System.err.println("Message body: " + (message != null ? new String(message.getBody()) : "null"));
            e.printStackTrace();
        }
    }
}
