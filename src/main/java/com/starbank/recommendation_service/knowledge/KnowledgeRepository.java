package com.starbank.recommendation_service.knowledge;

import java.math.BigDecimal;
import java.util.UUID;

public interface KnowledgeRepository {
    boolean isUserOf(UUID userId, String productType);
    int txCount(UUID userId, String productType);
    BigDecimal sumDeposit(UUID userId, String productType);
    BigDecimal sumWithdraw(UUID userId, String productType);
}
