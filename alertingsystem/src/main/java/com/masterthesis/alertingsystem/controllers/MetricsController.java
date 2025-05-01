package com.masterthesis.alertingsystem.controllers;

import com.masterthesis.alertingsystem.dtos.MetricResponseDto;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/queryMetrics")
public class MetricsController {

    private final DroolsRuleService droolsRuleService;
    public MetricsController(DroolsRuleService droolsRuleService) {
        this.droolsRuleService = droolsRuleService;
    }

    @GetMapping("/getMetric")
    public ResponseEntity<MetricResponseDto> getMetric(@RequestParam(name = "metricName", required = true) String metricName,
                                                       @RequestParam(name = "serviceName", required = true) String serviceName) {
        MetricResponseDto metricResponseDto = droolsRuleService.getMetric(serviceName, metricName);
        return new ResponseEntity<>(metricResponseDto, HttpStatus.OK);
    }

    @GetMapping("/getAllMetrics")
    public ResponseEntity<ArrayList<MetricResponseDto>> getMetrics(@RequestParam(name = "serviceName", required = true) String serviceName) {
        ArrayList<MetricResponseDto> metricResponseDtoList = droolsRuleService.getAllMetrics(serviceName);
        return new ResponseEntity<>(metricResponseDtoList, HttpStatus.OK);
    }

}
