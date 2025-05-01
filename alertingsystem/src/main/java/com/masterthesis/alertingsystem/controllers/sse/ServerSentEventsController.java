package com.masterthesis.alertingsystem.controllers.sse;

import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/metrics/sse")
public class ServerSentEventsController {

    private final DroolsRuleService droolsRuleService;

    public ServerSentEventsController(DroolsRuleService droolsRuleService) {
        this.droolsRuleService = droolsRuleService;
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @GetMapping("/pushAuthServiceMetrics")
    public SseEmitter streamMetricsForAuthService() {
        SseEmitter sseEmitter = new SseEmitter();
        executorService.execute(() -> {
            while(true) {
                ArrayList<MetricResponseDto> metricResponseDtoList = droolsRuleService.getAllMetrics("auth-service");
                try {
                    sseEmitter.send(SseEmitter.event().name("auth-service-metrics").data(metricResponseDtoList));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return sseEmitter;
    }

    @GetMapping("/pushInventoryServiceMetrics")
    public SseEmitter streamMetricsForInventoryService() {
        SseEmitter sseEmitter = new SseEmitter();
        executorService.execute(() -> {
            while(true) {
                ArrayList<MetricResponseDto> metricResponseDtoList = droolsRuleService.getAllMetrics("inventory-service");
                try {
                    sseEmitter.send(SseEmitter.event().name("inventory-service-metrics").data(metricResponseDtoList));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return sseEmitter;
    }
}
