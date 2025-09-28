package com.starbank.recommendation_service.dynamic.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dto.DynamicRuleRequest;
import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.dynamic.eval.RuleEvaluator;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import com.starbank.recommendation_service.dynamic.repository.DynamicRuleRepository;
import com.starbank.recommendation_service.model.ProductType;
import com.starbank.recommendation_service.model.UserFinancialData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RuleEvaluator evaluator;
    private final RuleStatsService stats;

    public DynamicRuleService(DynamicRuleRepository repository, RuleEvaluator evaluator,
                              RuleStatsService stats) {
        this.repository = repository;
        this.evaluator = evaluator;
        this.stats = stats;
    }

    @Transactional
    public DynamicRule create(DynamicRuleRequest dto) {
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

    public List<DynamicRule> listAll() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(DynamicRule::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteByProductId(UUID productId) {
        return repository.findByProductId(productId)
                .map(dr -> {
                    repository.delete(dr);
                    return true;
                })
                .orElse(false);
    }

    public List<RecommendationDto> evaluateDynamic(UUID userId) {
        List<com.starbank.recommendation_service.dto.RecommendationDto> out = new ArrayList<>();
        for (DynamicRule dr : repository.findAll()) {
            var conditions = com.starbank.recommendation_service.dynamic.mapper.DynamicRuleMapper.read(dr.getRuleJson());
            if (evaluator.matches(userId, conditions)) {
                stats.increment(dr.getId());
                out.add(new com.starbank.recommendation_service.dto.RecommendationDto(
                        dr.getProductId().toString(),
                        dr.getProductName(),
                        dr.getProductText()
                ));
            }
        }
        return out;
    }

    private boolean evaluate(DynamicRule dr, UserFinancialData data) {
        try {
            List<DynamicRuleRequest.RuleCondition> conds =
                    mapper.readValue(dr.getRuleJson(), new TypeReference<>() {
                    });
            for (DynamicRuleRequest.RuleCondition c : conds) {
                boolean q = switch (c.getQuery()) {
                    case "USER_OF" -> qUserOf(data, c.getArguments());
                    case "ACTIVE_USER_OF" -> qActiveUserOf(data, c.getArguments());
                    case "TRANSACTION_SUM_COMPARE" -> qSumCompare(data, c.getArguments());
                    case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> qSumCompareDepWith(data, c.getArguments());
                    default -> false;
                };
                if (c.isNegate()) q = !q;
                if (!q) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean qUserOf(UserFinancialData d, List<String> args) {
        ProductType type = ProductType.fromString(args.get(0));
        return d.getProductCountByType(type) > 0;
    }

    private boolean qActiveUserOf(UserFinancialData d, List<String> args) {
        ProductType type = ProductType.fromString(args.get(0));
        return d.getProductCountByType(type) > 0
                && (d.getDepositsByType(type) + d.getExpensesByType(type)) > 0;
    }

    private boolean qSumCompare(UserFinancialData d, List<String> args) {
        ProductType type = ProductType.fromString(args.get(0));
        String trxType = args.get(1);         // DEPOSIT | WITHDRAW
        String op = args.get(2);              // > < = >= <=
        int c = Integer.parseInt(args.get(3));

        double sum = "DEPOSIT".equalsIgnoreCase(trxType)
                ? d.getDepositsByType(type)
                : d.getExpensesByType(type);

        return cmp(sum, op, c);
    }

    private boolean qSumCompareDepWith(UserFinancialData d, List<String> args) {
        ProductType type = ProductType.fromString(args.get(0));
        String op = args.get(1);
        double dep = d.getDepositsByType(type);
        double wit = d.getExpensesByType(type);
        return cmp(dep, op, wit);
    }

    private boolean cmp(double left, String op, double right) {
        return switch (op) {
            case ">" -> left > right;
            case "<" -> left < right;
            case "=" -> Double.compare(left, right) == 0;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> false;
        };
    }
}
