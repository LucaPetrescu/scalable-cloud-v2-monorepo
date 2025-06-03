package com.masterthesis.alertingsystem.controllers.sse;

import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
                    ArrayList<MetricResponseDto> metricResponseDtoListForAuthService = new ArrayList<>();

                    for(MetricResponseDto metricResponseDto : metricResponseDtoList) {
                        if(metricResponseDto.getServiceName().equals("auth-service")){
                            metricResponseDtoListForAuthService.add(metricResponseDto);
                        }
                    }

                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name("auth-service-metrics")
                            .data(metricResponseDtoListForAuthService));
                        System.out.println("Auth " + metricResponseDtoListForAuthService.toString());
                        
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
                    ArrayList<MetricResponseDto> metricResponseDtoListForInventoryService = new ArrayList<>();

                    for(MetricResponseDto metricResponseDto : metricResponseDtoList){
                        if(metricResponseDto.getServiceName().equals("inventory-service")){
                            metricResponseDtoListForInventoryService.add(metricResponseDto);
                        }
                    }

                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name("inventory-service-metrics")
                            .data(metricResponseDtoListForInventoryService));
                        System.out.println("Inventory " + metricResponseDtoListForInventoryService.toString());
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

    @GetMapping("/pushAllServiceMetrics")
    public SseEmitter streamMetricsForAllServices() {
        System.out.println("Starting SSE connection for all services...");
        SseEmitter sseEmitter = new SseEmitter(30000L);

        executorService.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Fetching metrics for all services...");
                    ArrayList<MetricResponseDto> authMetrics = droolsRuleService.getAllMetrics("auth-service");
                    System.out.println("Auth metrics structure: " + authMetrics);
                    
                    ArrayList<MetricResponseDto> inventoryMetrics = droolsRuleService.getAllMetrics("inventory-service");
                    System.out.println("Inventory metrics structure: " + inventoryMetrics);

                    ArrayList<MetricResponseDto> allMetrics = new ArrayList<>();
                    allMetrics.addAll(authMetrics);
                    allMetrics.addAll(inventoryMetrics);
                    System.out.println("Combined metrics structure: " + allMetrics);

                    try {

                        ArrayList<ArrayList<MetricResponseDto>> wrappedMetrics = new ArrayList<>();
                        wrappedMetrics.add(new ArrayList<>());
                        wrappedMetrics.add(allMetrics);
                        
                        System.out.println("Sending wrapped metrics structure: " + wrappedMetrics);
                        sseEmitter.send(SseEmitter.event()
                            .name("all-service-metrics")
                            .data(wrappedMetrics));
                        System.out.println("Metrics sent successfully");
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        System.err.println("Error sending metrics via SSE: " + e.getMessage());
                        e.printStackTrace();
                        sseEmitter.completeWithError(e);
                        break;
                    } catch (InterruptedException e) {
                        System.err.println("SSE connection interrupted: " + e.getMessage());
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in SSE metrics stream: " + e.getMessage());
                e.printStackTrace();
                sseEmitter.completeWithError(e);
            }
        });

        sseEmitter.onCompletion(() -> {
            System.out.println("SSE connection completed");
        });

        sseEmitter.onTimeout(() -> {
            System.out.println("SSE connection timed out");
            sseEmitter.complete();
        });

        return sseEmitter;
    }
}
