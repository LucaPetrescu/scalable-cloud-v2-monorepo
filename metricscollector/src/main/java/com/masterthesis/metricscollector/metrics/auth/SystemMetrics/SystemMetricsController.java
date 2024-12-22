package com.masterthesis.metricscollector.metrics.auth.SystemMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/system-metrics")
public class SystemMetricsController {


    private KafkaTemplate<String, String> kafkaTemplate;

    public SystemMetricsController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    private String topicName = "auth-service-topic";

    @PostMapping("/cpu-metrics")
    public String getCpuMetrics(@RequestBody String cpuMetrics){

        if (cpuMetrics.isEmpty()) {
            throw new MetricReceivingException("[getCpuMetrics] Metric not received");
        }

        kafkaTemplate.send(topicName, 0, "cpu-usage", cpuMetrics);

        return cpuMetrics;
    }

    @PostMapping("/ram-metrics")
    public String getRamMetrics(@RequestBody String ramMetrics){

        if (ramMetrics.isEmpty()) {
            throw new MetricReceivingException("[getRamMetrics] Metric not received");
        }

        kafkaTemplate.send(topicName, 1, "ram-usage", ramMetrics);

        return ramMetrics;
    }

}
