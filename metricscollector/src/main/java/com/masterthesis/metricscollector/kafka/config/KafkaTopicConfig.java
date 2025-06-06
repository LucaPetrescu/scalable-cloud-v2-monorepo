package com.masterthesis.metricscollector.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic authServiceTopic() {
        return TopicBuilder.name("auth-service-topic").partitions(9).replicas(1).build();
    }

    @Bean
    public NewTopic inventoryServiceTopic() {
        return TopicBuilder.name("inventory-service-topic").partitions(9).replicas(1). build();
    }

}
