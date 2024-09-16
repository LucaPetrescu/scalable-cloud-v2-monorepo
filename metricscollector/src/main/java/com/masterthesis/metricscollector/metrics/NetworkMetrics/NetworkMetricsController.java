package com.masterthesis.metricscollector.metrics.NetworkMetrics;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/network-metrics")
public class NetworkMetricsController {

    @PostMapping("/http-request-count")
    public String getHttpRequestCount(@RequestBody String httpRequestCountMetrics){
        System.out.println("HTTP Request Count: " + httpRequestCountMetrics);
        return httpRequestCountMetrics;
    }

    @PostMapping("/http-request-duration")
    public String getHttpRequest(@RequestBody String httpRequestDurationMetrics){
        System.out.println("HTTP Request Duration: " + httpRequestDurationMetrics);
        return httpRequestDurationMetrics;
    }

}
