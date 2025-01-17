package com.masterthesis.metricscollector.metrics.auth.NetworkMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/network-metrics")
public class NetworkMetricsController {

    private KafkaTemplate<String, String> kafkaTemplate;

    public NetworkMetricsController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    private String topicName = "auth-service-topic";

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
