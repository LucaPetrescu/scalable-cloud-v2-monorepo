package com.masterthesis.alertingsystem.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisMessagePublisher implements MessagePublisher{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private List<ChannelTopic> topics = null;

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, List<ChannelTopic> topics) {
        this.redisTemplate = redisTemplate;
        this.topics = topics;
    }

    @Override
    public void publish(Message message){
        for(ChannelTopic t: topics) {
            System.out.println("Aici " + t.getTopic());
            if(t.getTopic().equals(message.getTopicName())){
                redisTemplate.convertAndSend(t.getTopic(), message);
            }
        }
    }

}
