package com.masterthesis.metricscollector.metrics.SystemMetrics;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/system-metrics")
public class SystemMetricsController {

    @PostMapping("/cpu-metrics")
    public String getCpuMetrics(@RequestBody String cpuMetrics){
        System.out.println("CPU Metrics: " + cpuMetrics);
        return cpuMetrics;
    }

    @PostMapping("/ram-metrics")
    public String getRamMetrics(@RequestBody String ramMetrics){
        System.out.println("RAM Metrics: " + ramMetrics);
        return ramMetrics;
    }

}
