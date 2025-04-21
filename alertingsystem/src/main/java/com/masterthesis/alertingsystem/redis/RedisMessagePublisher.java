package com.masterthesis.alertingsystem.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.ArrayList;
import java.util.List;

public class RedisMessagePublisher implements MessagePublisher{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    private List<ChannelTopic> topics = null;

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, List<ChannelTopic> topics) {
        this.redisTemplate = redisTemplate;
        this.topics = topics;
    }

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(String message){
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}
