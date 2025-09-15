package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.dynamic.service.DynamicRuleService;
import com.starbank.recommendation_service.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        var response = recommendationService.getRecommendationResponse(userId);
        var dynamic = dynamicRuleService.evaluateDynamic(userId);

        Map<String, RecommendationDto> merged = new LinkedHashMap<>();
        for (var r : response.getRecommendations()) merged.put(r.getId(), r);
        for (var r : dynamic) merged.putIfAbsent(r.getId(), r);

        response.setRecommendations(new ArrayList<>(merged.values()));
        return ResponseEntity.ok(response);
    }
}