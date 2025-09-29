package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.knowledge.KnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class CacheManagementController {

    @Autowired
    @Qualifier("knowledgeJdbc") // Предполагаем, что кешированный репозиторий использует делегат
    private KnowledgeRepository knowledgeRepository; // Это должен быть кеширующий делегат

    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCaches() {
        // Предположим, у кеширующего репозитория есть метод clear()
        if (knowledgeRepository instanceof com.starbank.recommendation_service.knowledge.KnowledgeRepositoryCached) {
            ((com.starbank.recommendation_service.knowledge.KnowledgeRepositoryCached) knowledgeRepository).clear();
        }
        // Или если метод есть в интерфейсе
        // knowledgeRepository.clear(); // Если добавили такой метод в интерфейс

        return ResponseEntity.ok("Caches cleared successfully.");
    }
}