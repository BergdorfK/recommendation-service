package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.dynamic.service.DynamicRuleService;
import com.starbank.recommendation_service.model.UserFinancialData;
import com.starbank.recommendation_service.repository.UserRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> recommendationRuleSets;
    private final UserRepository userRepository;
    private final DynamicRuleService dynamicRuleService;

    public RecommendationService(List<RecommendationRuleSet> recommendationRuleSets,
                                 UserRepository userRepository,
                                 DynamicRuleService dynamicRuleService) {
        this.recommendationRuleSets = recommendationRuleSets;
        this.userRepository = userRepository;
        this.dynamicRuleService = dynamicRuleService;
    }

    public RecommendationResponse getRecommendationResponse(UUID userId) {
        UserFinancialData financialData = userRepository.getUserFinancialData(userId);

        List<RecommendationDto> recommendations = new ArrayList<>();

        // Применяем фиксированные правила
        for (RecommendationRuleSet ruleSet : recommendationRuleSets) {
            ruleSet.apply(financialData).ifPresent(recommendations::add);
        }

        // Применяем динамические правила
        List<RecommendationDto> dynamicRecommendations = dynamicRuleService.evaluateDynamic(userId);
        recommendations.addAll(dynamicRecommendations);

        return new RecommendationResponse(userId.toString(), recommendations);
    }
}



