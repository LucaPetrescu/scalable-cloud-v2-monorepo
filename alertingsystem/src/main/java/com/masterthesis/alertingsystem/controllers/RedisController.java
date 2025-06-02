package com.masterthesis.alertingsystem.controllers.sse;

import com.masterthesis.alertingsystem.redis.RedisAlertCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisAlertCacheService redisAlertCacheService;

    @Autowired
    public RedisController(RedisAlertCacheService redisAlertCacheService) {
        this.redisAlertCacheService = redisAlertCacheService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllCachedData() {
        // This method should return all cached data as a list of maps
        List<Map<String, Object>> allData = redisAlertCacheService.getAllCachedData();
        return ResponseEntity.ok(allData);
    }

}
