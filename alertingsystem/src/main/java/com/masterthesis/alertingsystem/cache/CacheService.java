package com.masterthesis.alertingsystem.cache;

import com.masterthesis.alertingsystem.rules.facts.Alert;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

    @Autowired
    private MemcachedClient memcachedClient;

    private final int TTL = 259200;

    private final Set<String> cacheKeys = new HashSet<>();

    public void saveToCache(String key, Alert alert) {
        memcachedClient.set(key, TTL, alert);
        cacheKeys.add(key);
    }

    public Object getFromCache(String key) {
        return memcachedClient.get(key);
    }

    public Map<String, Object> getAllCachedItems() {
        Map<String, Object> allItems = new HashMap<>();

        System.out.println(cacheKeys);

        for (String key : cacheKeys) {
            Object cachedItem = memcachedClient.get(key);
            if (cachedItem != null) {
                allItems.put(key, cachedItem);
            } else {
                cacheKeys.remove(key);
            }
        }
        
        return allItems;
    }

    public List<Alert> getAllCachedAlerts() {
        List<Alert> alerts = new ArrayList<>();

        for (String key : cacheKeys) {
            Object cachedItem = memcachedClient.get(key);
            if (cachedItem instanceof Alert) {
                alerts.add((Alert) cachedItem);
            } else if (cachedItem == null) {
                cacheKeys.remove(key);
            }
        }
        
        return alerts;
    }

    public Set<String> getAllCacheKeys() {
        cacheKeys.removeIf(key -> memcachedClient.get(key) == null);
        return new HashSet<>(cacheKeys);
    }

    public void clearAllKeys() {
        cacheKeys.clear();
    }

    public void removeFromCache(String key) {
        memcachedClient.delete(key);
        cacheKeys.remove(key);
    }

}
