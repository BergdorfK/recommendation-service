package com.starbank.recommendation_service.dynamic.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rule_stats")
public class DynamicRuleStat {

    @Id
    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "count", nullable = false)
    private long count;

    protected DynamicRuleStat() {}

    public DynamicRuleStat(UUID ruleId, long count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public UUID getRuleId() { return ruleId; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}