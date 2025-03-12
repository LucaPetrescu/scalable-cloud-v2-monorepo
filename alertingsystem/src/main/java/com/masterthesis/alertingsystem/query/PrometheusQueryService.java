package com.masterthesis.alertingsystem.query;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PrometheusQueryService {

    private final WebClient webClient;

    private final String prometheusUrl = "http://localhost:9090/api/v1/query";

    public PrometheusQueryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(prometheusUrl).build();
    }

    public JsonNode query(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", query).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
