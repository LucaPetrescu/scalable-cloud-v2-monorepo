package com.masterthesis.metricscollector.metrics.SystemMetrics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/system-metrics")
public class SystemMetricsController {

    @GetMapping("/cpu-metrics")
    public void getCpuMetrics(){

    }

    @GetMapping("/ram-metrics")
    public void getRamMetrics(){

    }

}
