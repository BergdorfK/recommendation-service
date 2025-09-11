package com.starbank.recommendation_service.dynamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RuleQueryDto(
        @JsonProperty("query") String query,
        @JsonProperty("arguments") List<String> arguments,
        @JsonProperty("negate") boolean negate
) {}