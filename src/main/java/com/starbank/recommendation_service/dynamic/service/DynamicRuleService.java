package com.starbank.recommendation_service.dynamic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dto.DynamicRuleRequest;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import com.starbank.recommendation_service.dynamic.repository.DynamicRuleRepository;
import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.model.ProductType;
import com.starbank.recommendation_service.model.UserFinancialData;
import com.starbank.recommendation_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public DynamicRuleService(DynamicRuleRepository repository, UserRepository userRepository, ObjectMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public String convertRuleToJson(List<DynamicRuleRequest.RuleCondition> rule) throws JsonProcessingException {
        return mapper.writeValueAsString(rule);
    }

    public List<DynamicRuleRequest.RuleCondition> convertJsonToRule(String json) throws JsonProcessingException {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return mapper.readValue(json, new TypeReference<List<DynamicRuleRequest.RuleCondition>>() {});
    }

    public DynamicRule saveRule(DynamicRule rule) {
        return repository.save(rule);
    }

    public List<DynamicRule> getAllRules() {
        return repository.findAll();
    }

    public void deleteRuleByProductId(UUID productId) {
        Optional<DynamicRule> rule = repository.findByProductId(productId);
        rule.ifPresent(value -> repository.deleteById(value.getId()));
    }

    public List<RecommendationDto> evaluateDynamic(UUID userId) {
        UserFinancialData financialData = userRepository.getUserFinancialData(userId);
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (DynamicRule rule : repository.findAll()) {
            if (evaluateRule(rule, financialData)) {
                recommendations.add(new RecommendationDto(
                        rule.getProductId().toString(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }

        return recommendations;
    }

    private boolean evaluateRule(DynamicRule rule, UserFinancialData financialData) {
        try {
            if (rule.getRuleJson() == null || rule.getRuleJson().isEmpty()) {
                return true; // Правило без условий всегда выполняется
            }
            List<DynamicRuleRequest.RuleCondition> conditions = convertJsonToRule(rule.getRuleJson());
            for (DynamicRuleRequest.RuleCondition condition : conditions) {
                boolean result = evaluateCondition(condition, financialData);
                if (condition.isNegate()) {
                    result = !result;
                }
                if (!result) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean evaluateCondition(DynamicRuleRequest.RuleCondition condition, UserFinancialData financialData) {
        try {
            if (condition.getQuery() == null) {
                return false;
            }
            switch (condition.getQuery()) {
                case "USER_OF":
                    return evaluateUserOf(condition, financialData);
                case "ACTIVE_USER_OF":
                    return evaluateActiveUserOf(condition, financialData);
                case "TRANSACTION_SUM_COMPARE":
                    return evaluateTransactionSumCompare(condition, financialData);
                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                    return evaluateTransactionSumCompareDepositWithdraw(condition, financialData);
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean evaluateUserOf(DynamicRuleRequest.RuleCondition condition, UserFinancialData financialData) {
        if (condition.getArguments() == null || condition.getArguments().isEmpty()) return false;
        try {
            ProductType productType = ProductType.fromString(condition.getArguments().get(0));
            return financialData.hasProductType(productType);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateActiveUserOf(DynamicRuleRequest.RuleCondition condition, UserFinancialData financialData) {
        if (condition.getArguments() == null || condition.getArguments().isEmpty()) return false;
        try {
            ProductType productType = ProductType.fromString(condition.getArguments().get(0));
            return financialData.getProductCountByType(productType) >= 5;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateTransactionSumCompare(DynamicRuleRequest.RuleCondition condition, UserFinancialData financialData) {
        if (condition.getArguments() == null || condition.getArguments().size() < 4) return false;
        try {
            ProductType productType = ProductType.fromString(condition.getArguments().get(0));
            String transactionType = condition.getArguments().get(1);
            String operator = condition.getArguments().get(2);
            int compareValue = Integer.parseInt(condition.getArguments().get(3));

            double userValue;
            if ("DEPOSIT".equalsIgnoreCase(transactionType)) {
                userValue = financialData.getDepositsByType(productType);
            } else if ("WITHDRAW".equalsIgnoreCase(transactionType)) {
                userValue = financialData.getExpensesByType(productType);
            } else {
                return false;
            }

            return compareValues(userValue, operator, compareValue);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateTransactionSumCompareDepositWithdraw(DynamicRuleRequest.RuleCondition condition, UserFinancialData financialData) {
        if (condition.getArguments() == null || condition.getArguments().size() < 2) return false;
        try {
            ProductType productType = ProductType.fromString(condition.getArguments().get(0));
            String operator = condition.getArguments().get(1);

            double deposits = financialData.getDepositsByType(productType);
            double withdrawals = financialData.getExpensesByType(productType);

            return compareValues(deposits, operator, withdrawals);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean compareValues(double value1, String operator, double value2) {
        switch (operator) {
            case ">": return value1 > value2;
            case "<": return value1 < value2;
            case "=": return Math.abs(value1 - value2) < 0.001;
            case ">=": return value1 >= value2;
            case "<=": return value1 <= value2;
            default: return false;
        }
    }
}