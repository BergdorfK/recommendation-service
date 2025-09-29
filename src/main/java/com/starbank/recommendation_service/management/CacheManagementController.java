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
    private final List<CacheClearable> clearables;

    public CacheManagementController(Optional<CacheManager> cacheManager,
                                     List<CacheClearable> clearables) {
        this.cacheManager = cacheManager.orElse(null);
        this.clearables = clearables == null ? List.of() : clearables;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Map<String, Object>> clearCaches() {
        List<String> cleared = new ArrayList<>();

        // 1) наши кастомные кеши
        for (CacheClearable c : clearables) {
            try {
                c.clearCaches();
                cleared.add(c.name());
            } catch (Exception ex) {
                cleared.add(c.name() + " (ERROR: " + ex.getMessage() + ")");
            }
        }

        // 2) кеши Spring Cache, если есть
        int springCleared = 0;
        List<String> springCaches = new ArrayList<>();
        if (cacheManager != null) {
            for (String name : cacheManager.getCacheNames()) {
                Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear();
                    springCleared++;
                    springCaches.add(name);
                }
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("cleared_custom", cleared);
        body.put("cleared_spring_cache_count", springCleared);
        body.put("spring_caches", springCaches);
        return ResponseEntity.ok(body);
    }
}