package com.starbank.recommendation_service.knowledge;

import java.math.BigDecimal;
import java.util.UUID;

public interface KnowledgeRepository {
    boolean userOf(UUID userId, String productType);
    boolean activeUserOf(UUID userId, String productType, int days);
    BigDecimal sumByProductAndTxnKind(UUID userId, String productType, String txnKind);
    BigDecimal depositSum(UUID userId, String productType);
    BigDecimal withdrawSum(UUID u, String t);
}
