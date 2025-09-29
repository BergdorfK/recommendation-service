package com.starbank.recommendation_service.management;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/management")
public class CacheManagementController {

    private final CacheManager cacheManager;
    private final List<CacheClearable> manualClearables;

    public CacheManagementController(
            CacheManager cacheManager,
            ObjectProvider<List<CacheClearable>> clearablesProvider
    ) {
        this.cacheManager = cacheManager;
        this.manualClearables = Optional.ofNullable(clearablesProvider.getIfAvailable())
                .orElseGet(List::of);
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Map<String, Object>> clearCaches() {
        // 1) Spring CacheManager caches
        List<String> springCacheNames = cacheManager != null
                ? new ArrayList<>(cacheManager.getCacheNames())
                : List.of();
        int springCleared = 0;
        if (cacheManager != null) {
            for (String name : springCacheNames) {
                Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear();
                    springCleared++;
                }
            }
        }

        // 2) Ручные кеши (Caffeine) через CacheClearable
        List<String> manualNames = new ArrayList<>();
        for (CacheClearable c : manualClearables) {
            try {
                c.clearCaches();
                manualNames.add(c.name());
            } catch (Exception ex) {
                manualNames.add(c.name() + " (FAILED: " + ex.getClass().getSimpleName() + ")");
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("spring_caches_cleared", springCleared);
        body.put("spring_cache_names", springCacheNames);
        body.put("manual_caches_cleared", manualNames.size());
        body.put("manual_cache_beans", manualNames);
        return ResponseEntity.ok(body);
    }
}