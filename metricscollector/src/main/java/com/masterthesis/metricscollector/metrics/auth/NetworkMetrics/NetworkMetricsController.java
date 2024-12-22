package com.masterthesis.metricscollector.metrics.auth.NetworkMetrics;

import com.masterthesis.metricscollector.exceptions.MetricReceivingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/network-metrics")
public class NetworkMetricsController {

    @PostMapping("/http-request-count")
    public String getHttpRequestCount(@RequestBody String httpRequestCountMetrics) {

        if (httpRequestCountMetrics.isEmpty()) {
            throw new MetricReceivingException("[getHttpRequestCount] Metric not received");
        }

        return httpRequestCountMetrics;
    }

    @PostMapping("/http-request-duration")
    public String getHttpRequest(@RequestBody String httpRequestDurationMetrics) {

        if (httpRequestDurationMetrics.isEmpty()) {
            throw new MetricReceivingException("[getHttpRequest] Metric not received");
        }

        return httpRequestDurationMetrics;
    }

}
