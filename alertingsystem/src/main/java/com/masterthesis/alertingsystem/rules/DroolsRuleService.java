package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.masterthesis.alertingsystem.dtos.MetricsResponseDto;
import com.masterthesis.alertingsystem.query.PrometheusQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class DroolsRuleService {

    @Autowired
    private PrometheusQueryService queryClient;
    private DroolsRuleEngine droolsRuleEngine;


    @Scheduled(fixedRate = 5000)
    public ArrayList<MetricsResponseDto> getAllMetrics() {

        ArrayList<MetricsResponseDto> queriedMetrics = new ArrayList<>();

        String[] queries = {
                "cpu_usage_percent",
                "ram_usage_percent",
                "http_requests_total",
                "http_request_duration_seconds",
                "mongo_connection_pool_size",
                "mongo_mongo_active_connections",
                "mongo_available_connections",
                "mongo_query_time_seconds",
                "mongo_memory_usage_bytes"
            };

//        for(String query : queries) {
//            JsonNode metrics = queryClient.query(query);
//            Iterator<JsonNode> iterator = metrics.elements();
//
//            while(iterator.hasNext()){
//                JsonNode metricNode = iterator.next();
//                queriedMetrics.add(metricNode);
//            }
//        }

        JsonNode metric = queryClient.query(queries[0]);

        ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

        JsonNode metricResult = resultArrayNode.get(0);

        String metricName = metricResult.at("/metric/__name__").toString();

        String metricValue = metricResult.at("/value").get(1).toString();

        double metricValueDouble = Double.parseDouble(metricValue);

        MetricsResponseDto metricsResponse = new MetricsResponseDto(metricName, metricValueDouble);

        queriedMetrics.add(metricsResponse);

        return queriedMetrics;

    }

    public JsonNode getMetric(String metricName) {
        JsonNode metric = queryClient.query(metricName);

        System.out.println(metric);

        return metric;
    }

//    public MetricsResponseDto getMetricForService(String serviceName, String metricName) {
//        JsonNode metrics = queryClient.query(metricName);
//        Iterator<JsonNode> iterator = metrics.elements();
//
//        while(iterator.hasNext()){
//            JsonNode metricNode = iterator.next();
//            String metricNameResult = metricNode.path("metric").path("__name__").asText();
//            double metricValue = metricNode.path("value").get(1).asDouble();
//
//            return new MetricsResponseDto(metricNameResult, metricValue);
//
//        }
//
//        return null;
//    }


}
