package com.starbank.recommendation_service.knowledge.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.starbank.recommendation_service.knowledge.KnowledgeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class KnowledgeRepositoryImpl implements KnowledgeRepository {
    private final JdbcTemplate jdbc;
    private final Cache<String, Object> cache;

    public KnowledgeRepositoryImpl(JdbcTemplate jdbc, Cache<String, Object> cache) {
        this.jdbc = jdbc;
        this.cache = cache;
    }

    private static final String USER_OF_SQL = """
            SELECT COUNT(*) FROM TRANSACTIONS t
            JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
            WHERE t.USER_ID = ? AND p."TYPE" = ?""";

    private static final String SUM_DEPOSIT_SQL = """
            SELECT COALESCE(SUM(CASE WHEN t.AMOUNT > 0 THEN t.AMOUNT ELSE 0 END), 0)
            FROM TRANSACTIONS t JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
            WHERE t.USER_ID = ? AND p."TYPE" = ?""";

    private static final String SUM_WITHDRAW_SQL = """
            SELECT COALESCE(SUM(CASE WHEN t.AMOUNT < 0 THEN -t.AMOUNT ELSE 0 END), 0)
            FROM TRANSACTIONS t JOIN PRODUCTS p ON p.ID = t.PRODUCT_ID
            WHERE t.USER_ID = ? AND p."TYPE" = ?""";

    @Override
    public boolean isUserOf(UUID userId, String type) {
        String key = "userOf:" + userId + ":" + type;
        return (boolean) cache.get(key, k -> jdbc.queryForObject(USER_OF_SQL, Integer.class, userId, type) > 0);
    }

    @Override
    public int txCount(UUID userId, String type) {
        String key = "txCount:" + userId + ":" + type;
        return (int) cache.get(key, k -> jdbc.queryForObject(USER_OF_SQL, Integer.class, userId, type));
    }

    @Override
    public BigDecimal sumDeposit(UUID userId, String type) {
        String key = "sumDep:" + userId + ":" + type;
        return (BigDecimal) cache.get(key, k -> jdbc.queryForObject(SUM_DEPOSIT_SQL, BigDecimal.class, userId, type));
    }

    @Override
    public BigDecimal sumWithdraw(UUID userId, String type) {
        String key = "sumWdr:" + userId + ":" + type;
        return (BigDecimal) cache.get(key, k -> jdbc.queryForObject(SUM_WITHDRAW_SQL, BigDecimal.class, userId, type));
    }
}