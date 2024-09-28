package com.masterthesis.metricscollector.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${sping.kafka.bootstrap-servers}")
    private String bootstrapServers;

//    public Map<String, Object> producerConfig() {
//    }

}
