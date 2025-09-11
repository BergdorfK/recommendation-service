package com.starbank.recommendation_service.dynamic.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dynamic.dto.CreateRuleRequest;
import com.starbank.recommendation_service.dynamic.dto.RuleQueryDto;
import com.starbank.recommendation_service.dynamic.dto.RuleResponse;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;

import java.util.List;
import java.util.UUID;

public final class DynamicRuleMapper {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final TypeReference<List<RuleQueryDto>> RULE_LIST = new TypeReference<>() {};

    private DynamicRuleMapper() {}

    public static DynamicRule toEntity(CreateRuleRequest req) {
        DynamicRule e = new DynamicRule();
        e.setProductId(req.productId());
        e.setProductName(req.productName());
        e.setProductText(req.productText());
        e.setRuleJson(write(req.rule()));
        return e;
    }

    public static RuleResponse toResponse(DynamicRule e) {
        return new RuleResponse(
                e.getId(),
                e.getProductName(),
                e.getProductId(),
                e.getProductText(),
                read(e.getRuleJson())
        );
    }

    public static String write(List<RuleQueryDto> rule) {
        try {
            return OM.writeValueAsString(rule);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot serialize rule to JSON", ex);
        }
    }

    public static List<RuleQueryDto> read(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return OM.readValue(json, RULE_LIST);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot deserialize rule from JSON", ex);
        }
    }

    /** Если решим генерить UUID на уровне сервиса, а не БД */
    public static void assignIdIfAbsent(DynamicRule e) {
        if (e.getId() == null) e.setId(UUID.randomUUID());
    }
}