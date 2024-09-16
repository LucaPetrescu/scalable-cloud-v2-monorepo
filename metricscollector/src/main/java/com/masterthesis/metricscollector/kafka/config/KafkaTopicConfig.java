package com.masterthesis.metricscollector.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public NewTopic authServiceTopic() {
        return TopicBuilder.name("auth-service-topic").build();
    }

    public NewTopic inventoryServiceTopic(){
        return TopicBuilder.name("inventory-service-topic").build();
    }

}
