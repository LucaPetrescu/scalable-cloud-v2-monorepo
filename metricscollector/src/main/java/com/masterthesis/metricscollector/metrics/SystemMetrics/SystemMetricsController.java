package com.masterthesis.metricscollector.metrics.SystemMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/system-metrics")
public class SystemMetricsController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private String topicName = "auth-service-topic";

    @PostMapping("/cpu-metrics")
    public String getCpuMetrics(@RequestBody String cpuMetrics){

        if (cpuMetrics.isEmpty()) {
            throw new MetricReceivingException("[getCpuMetrics] Metric not received");
        }

//        System.out.println("CPU Metrics: " + cpuMetrics);

        kafkaTemplate.send(topicName, 0, "cpu-usage", cpuMetrics);

        return cpuMetrics;
    }

    @PostMapping("/ram-metrics")
    public String getRamMetrics(@RequestBody String ramMetrics){

        if (ramMetrics.isEmpty()) {
            throw new MetricReceivingException("[getRamMetrics] Metric not received");
        }

//        System.out.println("RAM Metrics: " + ramMetrics);

        kafkaTemplate.send(topicName, 1, "ram-usage", ramMetrics);

        return ramMetrics;
    }

}
