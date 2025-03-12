package com.masterthesis.alertingsystem.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query-metrics")
public class AuthServiceMetricsController {

    @GetMapping("/get-metric")
    public String getMetric(HttpServletRequest request) {
        return null;
    }

}
