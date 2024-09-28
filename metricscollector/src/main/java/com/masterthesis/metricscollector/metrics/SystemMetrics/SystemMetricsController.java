package com.masterthesis.metricscollector.metrics.SystemMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/system-metrics")
public class SystemMetricsController {

    @PostMapping("/cpu-metrics")
    public String getCpuMetrics(@RequestBody String cpuMetrics){

        if (cpuMetrics.equals("") || cpuMetrics == null) {
            throw new MetricReceivingException("[getCpuMetrics] Metric not received");
        }

        System.out.println("CPU Metrics: " + cpuMetrics);
        return cpuMetrics;
    }

    @PostMapping("/ram-metrics")
    public String getRamMetrics(@RequestBody String ramMetrics){

        if (ramMetrics.equals("") || ramMetrics == null) {
            throw new MetricReceivingException("[getRamMetrics] Metric not received");
        }

        System.out.println("RAM Metrics: " + ramMetrics);
        return ramMetrics;
    }

}
