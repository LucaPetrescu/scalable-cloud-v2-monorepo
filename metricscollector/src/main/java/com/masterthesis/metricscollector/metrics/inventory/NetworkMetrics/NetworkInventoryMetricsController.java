package com.masterthesis.metricscollector.metrics.inventory.NetworkMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inventory/network-metrics")
public class NetworkInventoryMetricsController {

    private KafkaTemplate<String, String> kafkaTemplate;

    public NetworkInventoryMetricsController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    private String topicName = "inventory-service-topic";


    @PostMapping("/http-request-count")
    public String getHttpRequestCount(@RequestBody String httpRequestCountMetrics) {

        if (httpRequestCountMetrics.isEmpty()) {
            throw new MetricReceivingException("[getHttpRequestCount] Metric not received");
        }

        kafkaTemplate.send(topicName, 2, "request_count", httpRequestCountMetrics);

        return httpRequestCountMetrics;
    }

    @PostMapping("/http-request-duration")
    public String getHttpRequest(@RequestBody String httpRequestDurationMetrics) {

        if (httpRequestDurationMetrics.isEmpty()) {
            throw new MetricReceivingException("[getHttpRequest] Metric not received");
        }

        kafkaTemplate.send(topicName, 3, "request_duration", httpRequestDurationMetrics);

        return httpRequestDurationMetrics;
    }


}
