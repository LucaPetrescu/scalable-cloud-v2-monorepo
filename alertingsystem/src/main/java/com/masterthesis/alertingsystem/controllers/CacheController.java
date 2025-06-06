package com.masterthesis.alertingsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterthesis.alertingsystem.cache.CacheService;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> getAllAlerts() {
        try {
            List<Alert> cachedAlerts = cacheService.getAllCachedAlerts();
            List<Map<String, Object>> alertMaps = new ArrayList<>();
            
            for (Alert alert : cachedAlerts) {
                Map<String, Object> alertMap = new HashMap<>();
                alertMap.put("service", alert.getServiceName());
                alertMap.put("reason", alert.getReason());
                alertMap.put("metricName", alert.getAffectedMetric());
                alertMap.put("metricValue", alert.getExceededValue());
                alertMaps.add(alertMap);
            }
            
            return new ResponseEntity<>(alertMaps, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error retrieving all alerts: " + e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCachedItems() {
        try {
            Map<String, Object> allItems = cacheService.getAllCachedItems();
            return new ResponseEntity<>(allItems, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error retrieving all cached items: " + e.getMessage());
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getAllCacheKeys() {
        try {
            Set<String> keys = cacheService.getAllCacheKeys();
            return new ResponseEntity<>(keys, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error retrieving cache keys: " + e.getMessage());
            return new ResponseEntity<>(new HashSet<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> get(@RequestParam String key) {
        try {
            Object retrievedItem = cacheService.getFromCache(key);
            if (retrievedItem != null) {
                return new ResponseEntity<>(retrievedItem, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Key not found or expired", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving item for key " + key + ": " + e.getMessage());
            return new ResponseEntity<>("Error retrieving item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key,
                     @RequestBody Alert alert) {
        try {
            cacheService.saveToCache(key, alert);
            return new ResponseEntity<>("Alert saved successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error saving alert to cache: " + e.getMessage());
            return new ResponseEntity<>("Error saving alert", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCache(@RequestParam String key) {
        try {
            cacheService.removeFromCache(key);
            return new ResponseEntity<>("Item removed successfully", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error removing item from cache: " + e.getMessage());
            return new ResponseEntity<>("Error removing item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/clear-keys")
    public ResponseEntity<String> clearAllKeys() {
        try {
            cacheService.clearAllKeys();
            return new ResponseEntity<>("All cache keys cleared", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error clearing cache keys: " + e.getMessage());
            return new ResponseEntity<>("Error clearing cache keys", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
