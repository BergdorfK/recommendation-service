package com.starbank.recommendation_service.dynamic.repository;

import java.util.UUID;

/**
 * Проекция для выборки статистики правил.
 * Используется в RuleStatsRepository.findAllWithRule().
 */
public interface RuleStatsView {
    UUID getRuleId();
    long getCount();
    String getProductCode();
    String getProductName();
}