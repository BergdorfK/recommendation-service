package com.starbank.recommendation_service.knowledge.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.starbank.recommendation_service.knowledge.KnowledgeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Repository("knowledgeJdbc")
public class KnowledgeRepositoryImpl implements KnowledgeRepository {

    private final JdbcTemplate jdbc;

    private final Cache<UserTypeKey, Boolean> userOfCache =
            Caffeine.newBuilder().maximumSize(50_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    private final Cache<UserTypeKey, Boolean> activeUserCache =
            Caffeine.newBuilder().maximumSize(50_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    private final Cache<UserTypeKindKey, BigDecimal> sumCache =
            Caffeine.newBuilder().maximumSize(100_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    public KnowledgeRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // === SQL ===
    private static final String COUNT_BY_PRODUCT_TYPE = """
        SELECT COUNT(*) 
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
        WHERE t.USER_ID = ? AND p."TYPE" = ?
        """;

    private static final String SUM_DEPOSIT = """
        SELECT COALESCE(SUM(CASE WHEN t.AMOUNT > 0 THEN t.AMOUNT ELSE 0 END), 0)
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
        WHERE t.USER_ID = ? AND p."TYPE" = ?
        """;

    private static final String SUM_WITHDRAW = """
        SELECT COALESCE(SUM(CASE WHEN t.AMOUNT < 0 THEN -t.AMOUNT ELSE 0 END), 0)
        FROM TRANSACTIONS t
        JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
        WHERE t.USER_ID = ? AND p."TYPE" = ?
        """;

    @Override
    public boolean userOf(UUID userId, String productType) {
        return userOfCache.get(new UserTypeKey(userId, productType), key -> {
            Integer cnt = jdbc.queryForObject(COUNT_BY_PRODUCT_TYPE, Integer.class, key.userId(), key.productType());
            return cnt != null && cnt > 0;
        });
    }

    @Override
    public boolean activeUserOf(UUID userId, String productType, int minCount) {
        return activeUserCache.get(new UserTypeKey(userId, productType), key -> {
            Integer cnt = jdbc.queryForObject(COUNT_BY_PRODUCT_TYPE, Integer.class, key.userId(), key.productType());
            return cnt != null && cnt >= minCount;
        });
    }

    @Override
    public BigDecimal sumByProductAndTxnKind(UUID userId, String productType, String txnKind) {
        return sumCache.get(new UserTypeKindKey(userId, productType, txnKind), key -> {
            String kind = key.txnKind().toUpperCase();
            String sql = switch (kind) {
                case "DEPOSIT" -> SUM_DEPOSIT;
                case "WITHDRAW" -> SUM_WITHDRAW;
                default -> throw new IllegalArgumentException("Unknown txn kind: " + kind);
            };
            BigDecimal v = jdbc.queryForObject(sql, BigDecimal.class, key.userId(), key.productType());
            return v == null ? BigDecimal.ZERO : v;
        });
    }

    @Override public BigDecimal depositSum(UUID u, String t)  { return sumByProductAndTxnKind(u, t, "DEPOSIT"); }
    @Override public BigDecimal withdrawSum(UUID u, String t) { return sumByProductAndTxnKind(u, t, "WITHDRAW"); }

    // === cache keys ===
    private record UserTypeKey(UUID userId, String productType) {
        UserTypeKey { Objects.requireNonNull(userId); Objects.requireNonNull(productType); }
    }
    private record UserTypeKindKey(UUID userId, String productType, String txnKind) {
        UserTypeKindKey { Objects.requireNonNull(userId); Objects.requireNonNull(productType); Objects.requireNonNull(txnKind); }
    }
}
