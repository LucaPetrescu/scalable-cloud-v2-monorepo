package com.masterthesis.alertingsystem.redis;

import com.masterthesis.alertingsystem.rules.facts.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisAlertCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisAlertCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheAlert(String key, Alert alert, int ttlInDays) {
        redisTemplate.opsForValue().set(key, alert, Duration.ofDays(ttlInDays));
    }

    public Alert getCachedAlert(String key) {
        Object cached = redisTemplate.opsForValue().get(key);
        return cached instanceof Alert ? (Alert) cached : null;
    }

    public void removeAlert(String key) {
        redisTemplate.delete(key);
    }

    public boolean isAlertCached(String key) {
        return redisTemplate.hasKey(key);
    }

}
