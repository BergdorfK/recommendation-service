package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.model.UserFinancialData;

import java.util.Optional;

public interface RecommendationRuleSet {
    Optional<RecommendationDto> apply(UserFinancialData financialData);
}