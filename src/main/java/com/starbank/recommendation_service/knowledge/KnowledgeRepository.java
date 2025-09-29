package com.starbank.recommendation_service.knowledge;

import java.math.BigDecimal;
import java.util.UUID;

public interface KnowledgeRepository {
    boolean userOf(UUID userId, String productType);
    boolean activeUserOf(UUID userId, String productType, int days);
    java.math.BigDecimal sumByProductAndTxnKind(UUID userId, String productType, String txnKind);
    java.math.BigDecimal depositSum(UUID userId, String productType);
    // Метод для очистки кеша (если нужно, чтобы был доступен через интерфейс)
    default void clear() {
        // Реализация по умолчанию, если не переопределена
    }

    BigDecimal withdrawSum(UUID u, String t);
}
