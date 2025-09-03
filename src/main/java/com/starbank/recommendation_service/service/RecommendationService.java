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

    private final List<RecommendationRuleSet> rules;
    private final UserRepository userRepository;
    private final DynamicRuleService dynamicRuleService;

    public RecommendationService(List<RecommendationRuleSet> rules,
                                 UserRepository userRepository,
                                 DynamicRuleService dynamicRuleService) {
        this.rules = rules;
        this.userRepository = userRepository;
        this.dynamicRuleService = dynamicRuleService;
    }

    public RecommendationResponse getRecommendationResponse(UUID userId) {
        UserFinancialData financialData = fetchUserOr404(userId);

        // 1 Статические рекомендации
        List<RecommendationDto> staticRecs = rules.stream()
                .map(rule -> rule.apply(financialData))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        // 2 Динамические рекомендации
        List<RecommendationDto> dynamicRecs = dynamicRuleService.evaluateDynamic(financialData);

        // 3 Склейка приоритет у статических — они кладутся первыми)
        LinkedHashMap<String, RecommendationDto> byId = new LinkedHashMap<>();
        for (RecommendationDto r : staticRecs) {
            if (notBlank(r.getId())) byId.putIfAbsent(r.getId(), r);
        }
        for (RecommendationDto r : dynamicRecs) {
            if (notBlank(r.getId())) byId.putIfAbsent(r.getId(), r);
        }

        List<RecommendationDto> merged = new ArrayList<>(byId.values());
        return new RecommendationResponse(userId.toString(), merged);
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private UserFinancialData fetchUserOr404(UUID userId) {
        try {
            UserFinancialData data = userRepository.getUserFinancialData(userId);
            if (data == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
            return data;
        } catch (DataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "H2 query failed: " + e.getMostSpecificCause().getMessage(), e
            );
        }
    }
}
