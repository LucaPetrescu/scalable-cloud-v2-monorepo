package com.masterthesis.alertingsystem.controllers.sse;

import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/metrics/sse")
public class ServerSentEventsController {

    @Autowired
    private final DroolsRuleService droolsRuleService;

    public ServerSentEventsController(DroolsRuleService droolsRuleService) {
        this.droolsRuleService = droolsRuleService;
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @GetMapping("/pushAuthServiceMetrics")
    public SseEmitter streamMetricsForAuthService() {
        // Set a longer timeout (e.g., 30 seconds)
        SseEmitter sseEmitter = new SseEmitter(30000L);
        
        executorService.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ArrayList<MetricResponseDto> metricResponseDtoList = droolsRuleService.getAllMetrics("auth-service");
                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name("auth-service-metrics")
                            .data(metricResponseDtoList));
                        
                        // Add a small delay to prevent overwhelming the connection
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                        break;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });

        // Handle client disconnection
        sseEmitter.onCompletion(() -> {
            // Clean up resources if needed
        });

        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
        });

        return sseEmitter;
    }

    @GetMapping("/pushInventoryServiceMetrics")
    public SseEmitter streamMetricsForInventoryService() {
        SseEmitter sseEmitter = new SseEmitter(30000L);
        
        executorService.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ArrayList<MetricResponseDto> metricResponseDtoList = droolsRuleService.getAllMetrics("inventory-service");
                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name("inventory-service-metrics")
                            .data(metricResponseDtoList));
                        
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                        break;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });

        sseEmitter.onCompletion(() -> {
            // Clean up resources if needed
        });

        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
        });

        return sseEmitter;
    }
}
