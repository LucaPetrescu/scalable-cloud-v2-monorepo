package com.masterthesis.alertingsystem.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PrometheusQueryClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String prometheusUrl = "http://localhost:9090/api/v1/query";

    public PrometheusQueryClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode query(String query) {
        String url = prometheusUrl + "?query=" + query;
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse.path("data").path("result");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Prometheus response", e);
        }
    }
}
