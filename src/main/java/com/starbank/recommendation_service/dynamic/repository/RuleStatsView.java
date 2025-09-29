package com.starbank.recommendation_service.dynamic.repository;

// Предположим, что это record для представления данных из запроса
public interface RuleStatsView {
    String getRuleId();
    Long getCount();
    String getProductCode();
    String getProductName();
}