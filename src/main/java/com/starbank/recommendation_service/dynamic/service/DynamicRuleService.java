package com.starbank.recommendation_service.dynamic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.dynamic.eval.RuleEvaluator;
import com.starbank.recommendation_service.dynamic.mapper.DynamicRuleMapper;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;

import com.starbank.recommendation_service.dynamic.repository.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RuleEvaluator evaluator;
    private final RuleStatsService stats;

    public DynamicRuleService(DynamicRuleRepository repository,
                              RuleEvaluator evaluator,
                              RuleStatsService stats) {
        this.repository = repository;
        this.evaluator = evaluator;
        this.stats = stats;
    }

    @Transactional(transactionManager = "rulesTransactionManager")
    public DynamicRule create(com.starbank.recommendation_service.dto.DynamicRuleRequest dto) {
        UUID productId = UUID.fromString(dto.getProductId());
        String ruleJson;
        try {
            ruleJson = mapper.writeValueAsString(dto.getRule());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid rule JSON: " + e.getMessage(), e);
        }
        DynamicRule rule = new DynamicRule();
        rule.setProductId(productId);
        rule.setProductName(dto.getProductName());
        rule.setProductText(dto.getProductText());
        rule.setRuleJson(ruleJson);
        return repository.save(rule);
    }

    @Transactional(readOnly = true, transactionManager = "rulesTransactionManager")
    public List<DynamicRule> listAll() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(DynamicRule::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Transactional(transactionManager = "rulesTransactionManager")
    public boolean deleteByProductId(UUID productId) {
        return repository.findByProductId(productId)
                .map(dr -> {
                    repository.delete(dr); // счетчик удалится каскадом благодаря FK ON DELETE CASCADE
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true, transactionManager = "rulesTransactionManager")
    public List<RecommendationDto> evaluateDynamic(UUID userId) {
        List<RecommendationDto> out = new ArrayList<>();
        for (DynamicRule dr : repository.findAll()) {
            var conditions = DynamicRuleMapper.read(dr.getRuleJson());
            if (evaluator.matches(userId, conditions)) {
                out.add(new RecommendationDto(
                        dr.getProductId().toString(),
                        dr.getProductName(),
                        dr.getProductText()
                ));
                // инкремент статистики
                stats.increment(dr.getId());
            }
        }
        return out;
    }
}