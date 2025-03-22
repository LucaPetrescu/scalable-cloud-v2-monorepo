package com.masterthesis.alertingsystem.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.masterthesis.alertingsystem.dtos.MetricsResponseDto;
import com.masterthesis.alertingsystem.query.MetricType;
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

        for(MetricType metricType: MetricType.values()){
            try{
                JsonNode metric = queryClient.query(metricType.getQueryName());
                ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

                if(!resultArrayNode.isEmpty()) {
                    JsonNode metricResult = resultArrayNode.get(0);

                    String metricName = metricResult.at("/metric/__name__").toString();
                    String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                    double metricValueDouble = Double.parseDouble(metricValue);

                    MetricsResponseDto metricsResponse = new MetricsResponseDto(metricName, metricValueDouble, metricType.getDisplayName(), metricType.getUnit());
                    queriedMetrics.add(metricsResponse);
                }
            } catch (Exception e){

            }
        }

        return queriedMetrics;

    }

    public MetricsResponseDto getMetric(String metricQuery) {

        MetricsResponseDto metricsResponseDto = null;

        try{
            JsonNode metric = queryClient.query(metricQuery);
            ArrayNode resultArrayNode = (ArrayNode) metric.at("/data/result");

            if(!resultArrayNode.isEmpty()){
                JsonNode metricResult = resultArrayNode.get(0);

                String metricName = metricResult.at("/metric/__name__").toString();
                String metricValue = metricResult.at("/value").get(1).toString().replace("\"", "");
                double metricValueDouble = Double.parseDouble(metricValue);

                MetricType metricType = MetricType.fromQueryName(metricQuery);

                metricsResponseDto = new MetricsResponseDto(metricName, metricValueDouble, metricType.getDisplayName(), metricType.getUnit());

            }

            return metricsResponseDto;

        } catch(Exception e){
            System.err.println("Error querying metric " + metricQuery + ": " + e.getMessage());
            return null;
        }
    }

}
