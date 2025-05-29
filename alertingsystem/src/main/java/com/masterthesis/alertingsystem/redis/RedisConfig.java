package com.masterthesis.alertingsystem.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterthesis.alertingsystem.redis.utils.AlertMessage;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.masterthesis.alertingsystem.redis.websocket.WebSocketService;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        // Configure type handling for AlertMessage and Alert classes
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(AlertMessage.class)
                .allowIfBaseType(Alert.class)
                .build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, authServiceAlertsTopic());
        container.addMessageListener(messageListenerAdapter, inventoryServiceAlertsTopic());
        return container;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(redisCacheConfiguration).build();
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
    public MessageListenerAdapter messageListenerAdapter(ObjectMapper objectMapper, WebSocketService webSocketService) {
        return new MessageListenerAdapter(new RedisMessageSubscriber(objectMapper, webSocketService), "onMessage");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Configure serializers
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // Use StringRedisSerializer for keys
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Use Jackson2JsonRedisSerializer for values
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        // Set default serializer
        redisTemplate.setDefaultSerializer(serializer);

        // Configure message serializer for pub/sub
        redisTemplate.setStringSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public MessagePublisher messagePublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        List<ChannelTopic> topics = new ArrayList<>();
        topics.add(authServiceAlertsTopic());
        topics.add(inventoryServiceAlertsTopic());
        return new RedisMessagePublisher(redisTemplate, topics, objectMapper);
    }

}
