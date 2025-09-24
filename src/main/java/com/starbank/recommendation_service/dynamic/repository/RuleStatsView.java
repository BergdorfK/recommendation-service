package com.starbank.recommendation_service.dynamic.repository;

import java.util.UUID;

public interface RuleStatsView {
    UUID getRuleId();
    long getCount();
    String getProductCode();
    String getProductName();
}