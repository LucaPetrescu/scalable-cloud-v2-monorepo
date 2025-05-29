package com.masterthesis.alertingsystem.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterthesis.alertingsystem.redis.utils.AlertMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisMessagePublisher implements MessagePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private List<ChannelTopic> topics = null;

    private final ObjectMapper objectMapper;

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, List<ChannelTopic> topics, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.topics = topics;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(AlertMessage message, String topicName) {
        try {
            ChannelTopic targetTopic = topics.stream()
                    .filter(topic -> topic.getTopic().equals(topicName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No topic found for " + topicName));

            System.out.println("Publishing message: " + message);

            redisTemplate.convertAndSend(targetTopic.getTopic(), message);
        } catch (Exception e) {
            System.err.println("Error publishing Redis message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
