package com.starbank.recommendation_service.dynamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RuleStatDto(
        @JsonProperty("rule_id") UUID rule_id,
        @JsonProperty("count") long count
) {}
