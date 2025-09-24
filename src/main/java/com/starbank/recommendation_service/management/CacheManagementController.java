package com.starbank.recommendation_service.management;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/management")
public class CacheManagementController {

    private final CacheManager cacheManager;

    public CacheManagementController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Map<String, Object>> clearCaches() {
        List<String> names = new ArrayList<>(cacheManager.getCacheNames());
        int cleared = 0;
        for (String name : names) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
                cleared++;
            }
        }
        Map<String, Object> body = Map.of(
                "cleared", cleared,
                "caches", names
        );
        return ResponseEntity.ok(body);
    }
}