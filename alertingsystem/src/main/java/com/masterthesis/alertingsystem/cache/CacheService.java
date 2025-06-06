package com.masterthesis.alertingsystem.cache;

import com.masterthesis.alertingsystem.rules.facts.Alert;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private MemcachedClient memcachedClient;

    private final int TTL = 259200;

    public void saveToCache(String key, Alert alert) {
        System.out.println("[saveToCache] Saving alert to cache with key: " + key);
        System.out.println("[saveToCache] Alert details: " + alert.toString());
        try {
            boolean success = memcachedClient.set(key, TTL, alert).getStatus().isSuccess();
            System.out.println(success);
        } catch (Exception e) {
            System.err.println("Error saving to cache: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Object getFromCache(String key) {
        try {
            Object alert = memcachedClient.get(key);
            return  alert;
        } catch (Exception e) {
            System.err.println("Error retrieving from cache: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAlertCached(String key) {
        try {
            Object value = memcachedClient.get(key);
            boolean exists = value != null;
            return exists;
        } catch (Exception e) {
            System.err.println("Error checking cache: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
