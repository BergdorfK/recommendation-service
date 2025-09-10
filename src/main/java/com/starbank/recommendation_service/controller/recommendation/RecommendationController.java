package com.starbank.recommendation_service.controller.recommendation;

import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.service.RecommendationService;
import com.starbank.recommendation_service.dynamic.service.DynamicRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final DynamicRuleService dynamicRuleService;

    public RecommendationController(RecommendationService recommendationService,
                                    DynamicRuleService dynamicRuleService) {
        this.recommendationService = recommendationService;
        this.dynamicRuleService = dynamicRuleService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable UUID userId) {
        // Получаем рекомендации от фиксированных правил
        var response = recommendationService.getRecommendationResponse(userId);

        // Добавляем динамические рекомендации
        var dynamicRecommendations = dynamicRuleService.evaluateDynamic(userId);
        response.getRecommendations().addAll(dynamicRecommendations);

        return ResponseEntity.ok(response);
    }
}