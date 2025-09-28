package com.starbank.recommendation_service.management;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.ObjectProvider;

import java.util.*;

@RestController
@RequestMapping("/management")
public class CacheManagementController {

    private final ObjectProvider<CacheManager> cacheManagerProvider;
    private final List<CacheClearable> manualCaches;

    public CacheManagementController(ObjectProvider<CacheManager> cacheManagerProvider,
                                     List<CacheClearable> manualCaches) {
        this.cacheManagerProvider = cacheManagerProvider;
        this.manualCaches = manualCaches;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Map<String, Object>> clearCaches() {
        int cleared = 0;
        List<String> names = new ArrayList<>();

        CacheManager cacheManager = cacheManagerProvider.getIfAvailable();
        if (cacheManager != null) {
            names.addAll(cacheManager.getCacheNames());
            for (String name : names) {
                Cache cache = cacheManager.getCache(name);
                if (cache != null) { cache.clear(); cleared++; }
            }
        }

        List<String> manual = new ArrayList<>();
        for (CacheClearable c : manualCaches) {
            try { c.clearCaches(); manual.add(c.name()); } catch (Exception ignored) {}
        }

        Map<String, Object> body = Map.of(
                "springCachesCleared", cleared,
                "springCaches", names,
                "manualCachesCleared", manual.size(),
                "manualCaches", manual
        );
        return ResponseEntity.ok(body);
    }
}