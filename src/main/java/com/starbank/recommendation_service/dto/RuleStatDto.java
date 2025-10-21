package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class RuleStatDto {

    @JsonProperty("rule_id")
    private UUID ruleId;

    @JsonProperty("count")
    private Long count;

    public RuleStatDto() {}

    public RuleStatDto(UUID ruleId, Long count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}