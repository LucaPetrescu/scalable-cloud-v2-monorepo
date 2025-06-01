package com.masterthesis.alertingsystem.controllers.sse;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisAlertCacheService redisAlertCacheService;

    @Autowired
    public CacheController(RedisAlertCacheService redisAlertCacheService) {
        this.redisAlertCacheService = redisAlertCacheService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllCachedData() {
        // This method should return all cached data as a list of maps
        List<Map<String, Object>> allData = redisAlertCacheService.getAllCachedData();
        return ResponseEntity.ok(allData);
    }

}
