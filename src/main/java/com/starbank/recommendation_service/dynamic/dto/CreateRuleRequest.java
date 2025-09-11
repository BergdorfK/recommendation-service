package com.starbank.recommendation_service.dynamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateRuleRequest(
        @JsonProperty("product_name") @NotBlank String productName,
        @JsonProperty("product_id")   @NotNull  UUID   productId,
        @JsonProperty("product_text") @NotBlank String productText,
        @JsonProperty("rule")         @NotNull  List<RuleQueryDto> rule
) {}