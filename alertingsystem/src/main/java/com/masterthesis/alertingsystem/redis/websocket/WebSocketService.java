package com.masterthesis.alertingsystem.redis.websocket;

import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.redis.utils.AlertMessage;
import com.masterthesis.alertingsystem.redis.utils.AlertNotification;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class WebSocketService {

    private final DroolsRuleService droolsRuleService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(DroolsRuleService droolsRuleService, SimpMessagingTemplate simpMessagingTemplate) {
        this.droolsRuleService = droolsRuleService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendAlert(Alert alert, String serviceName) {
        System.out.println("Sending alert via WebSocket: " + alert.getReason());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/alerts", new AlertNotification(alert, serviceName, time));
    }

}
