package com.starbank.recommendation_service.dynamic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dynamic_rule_stats")
public class DynamicRuleStat {

    @Id
    @Column(name = "rule_id", nullable = false)
    private String ruleId; // Используем String, так как ID правила может быть UUID

    @Column(name = "count", nullable = false)
    private Long count = 0L; // Инициализируем 0

    // Конструкторы
    public DynamicRuleStat() {}

    public DynamicRuleStat(String ruleId) {
        this.ruleId = ruleId;
    }

    // Геттеры и сеттеры
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    // Метод для инкремента
    public void increment() {
        this.count++;
    }
}