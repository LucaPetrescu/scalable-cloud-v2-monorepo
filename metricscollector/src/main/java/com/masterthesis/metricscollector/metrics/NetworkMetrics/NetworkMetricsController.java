package com.masterthesis.metricscollector.metrics.NetworkMetrics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/network-metrics")
public class NetworkMetricsController {

    @GetMapping("/http-request-count")
    public void getHttpRequestCount(){

    }

    @GetMapping("/http-request-duration")
    public void getHttpRequest(){

    }

}
