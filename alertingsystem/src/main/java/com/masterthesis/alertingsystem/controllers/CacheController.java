package com.masterthesis.alertingsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterthesis.alertingsystem.cache.CacheService;
import com.masterthesis.alertingsystem.rules.facts.Alert;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> alerts = new ArrayList<>();
        String[] services = {"auth-service", "inventory-service"};

        for (String service : services) {
            try {
                Alert alert = (Alert) cacheService.getFromCache(service);
                if (alert != null) {
                    Map<String, Object> alertMap = new HashMap<>();
                    alertMap.put("service", service);
                    alertMap.put("reason", alert.getReason());
                    alertMap.put("metricName", alert.getAffectedMetric());
                    alertMap.put("metricValue", alert.getExceededValue());
                    alerts.add(alertMap);
                }
            } catch (Exception e) {
                System.err.println("Error retrieving alert for service " + service + ": " + e.getMessage());
            }
        }

        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<Object> get(@RequestParam String key) {
        Object retrievedAlert = cacheService.getFromCache(key);
        return new ResponseEntity<>(retrievedAlert, HttpStatus.OK);
    }

    @PostMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key,
                     @RequestBody String value,
                     @RequestParam(defaultValue = "300") int ttlSeconds) {
        cacheService.saveToCache(key, new Alert());

        return new ResponseEntity<>("Value saved succesfully", HttpStatus.CREATED);
    }
    
}
