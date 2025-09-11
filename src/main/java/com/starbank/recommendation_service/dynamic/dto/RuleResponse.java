package com.starbank.recommendation_service.dynamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record RuleResponse(
        @JsonProperty("id")           UUID id,
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_id")   UUID productId,
        @JsonProperty("product_text") String productText,
        @JsonProperty("rule")         List<RuleQueryDto> rule
) {}