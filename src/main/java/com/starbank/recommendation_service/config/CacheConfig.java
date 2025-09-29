package com.starbank.recommendation_service.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String KNOWLEDGE_CACHE = "knowledgeCache";

    /** Spring CacheManager, который нужен контроллеру */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(KNOWLEDGE_CACHE);
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(Duration.ofMinutes(10))
        );
        return manager;
    }

    /** Опционально: «сырой» Caffeine Cache, если где-то инжектится напрямую */
    @Bean(name = "knowledgeCacheRaw")
    public Cache<String, Object> knowledgeCacheRaw() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .build();
    }
}