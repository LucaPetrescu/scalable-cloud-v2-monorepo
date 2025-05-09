package com.masterthesis.alertingsystem.redis.websocket;

import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class WebSocketService {

    private final DroolsRuleService droolsRuleService;

    private SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(DroolsRuleService droolsRuleService, SimpMessagingTemplate simpMessagingTemplate) {
        this.droolsRuleService = droolsRuleService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

//    public void sendAlertsForAuthService() {
//        ArrayList<MetricResponseDto> metricResponseDtoArrayList = droolsRuleService.getAllMetrics("auth-service");
//        String time = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//        simpMessagingTemplate.convertAndSend("/topic/alertsForAuthService", );
//    }

}
