package com.masterthesis.alertingsystem.cache.websocket;

import com.masterthesis.alertingsystem.cache.utils.AlertMessage;
import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.cache.utils.AlertNotification;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class WebSocketService {

    private final DroolsRuleService droolsRuleService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String[] SERVICES = {"auth-service", "inventory-service"};

    public WebSocketService(DroolsRuleService droolsRuleService, SimpMessagingTemplate simpMessagingTemplate) {
        this.droolsRuleService = droolsRuleService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendAlert(AlertMessage alertMessage, String serviceName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/alerts", new AlertNotification(alertMessage.getAlert(), serviceName, time));
    }

//    @Scheduled(fixedRate = 5000)
//    public void checkMetricsAndSendNotifications() {
//        for (String service : SERVICES) {
//            try {
//                List<MetricResponseDto> metrics = droolsRuleService.getAllMetrics(service);
//            } catch (Exception e) {
//                System.err.println("Error checking metrics for " + service + ": " + e.getMessage());
//            }
//        }
//    }
}
