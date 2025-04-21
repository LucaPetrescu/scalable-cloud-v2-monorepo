package com.masterthesis.alertingsystem.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.util.ArrayList;
import java.util.List;

public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListenerAdapter) {

        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();

        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, authServiceAlertsTopic());
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, inventoryServiceAlertsTopic());

        return redisMessageListenerContainer;
    }

    @Bean
    public ChannelTopic authServiceAlertsTopic() {
        return new ChannelTopic("auth-service-alerts-topic");
    }

    @Bean
    public ChannelTopic inventoryServiceAlertsTopic() {
        return new ChannelTopic("inventory-service-alerts-topic");
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new RedisMessageSubscriber(), "onMessage");
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));

        return redisTemplate;
    }

    @Bean
    public MessagePublisher messagePublisher() {

        List<ChannelTopic> topics = new ArrayList<>();

        topics.add(authServiceAlertsTopic());
        topics.add(inventoryServiceAlertsTopic());

        return new RedisMessagePublisher(redisTemplate(redisConnectionFactory), topics);
    }

}
