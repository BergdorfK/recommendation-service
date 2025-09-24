package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.dto.RecommendationResponse;
import com.starbank.recommendation_service.model.UserFinancialData;
import com.starbank.recommendation_service.repository.UserRepository;
import com.starbank.recommendation_service.rules.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> rules;
    private final UserRepository userRepository;

    public RecommendationService(List<RecommendationRuleSet> rules, UserRepository userRepository) {
        this.rules = rules;
        this.userRepository = userRepository;
    }

    public RecommendationResponse getRecommendationResponse(UUID userId) {
        UserFinancialData financialData = userRepository.getUserFinancialData(userId);

        List<RecommendationDto> recommendations = rules.stream()
                .map(rule -> rule.apply(financialData))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return new RecommendationResponse(userId.toString(), recommendations);
    }

}