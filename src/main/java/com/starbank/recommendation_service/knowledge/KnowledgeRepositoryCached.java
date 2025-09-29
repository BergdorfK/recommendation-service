package com.starbank.recommendation_service.knowledge;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.starbank.recommendation_service.management.CacheClearable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Primary
@Service
public class KnowledgeRepositoryCached implements KnowledgeRepository, CacheClearable {

    private final KnowledgeRepository delegate;

    public KnowledgeRepositoryCached(@Qualifier("knowledgeJdbc") KnowledgeRepository delegate) {
        this.delegate = delegate;
    }

    private final Cache<UserTypeKey, Boolean> userOfCache =
            Caffeine.newBuilder().maximumSize(50_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    private final Cache<UserTypeKey, Boolean> activeUserCache =
            Caffeine.newBuilder().maximumSize(50_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    private final Cache<UserTypeKindKey, BigDecimal> sumCache =
            Caffeine.newBuilder().maximumSize(100_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    @Override
    public boolean userOf(UUID userId, String productType) {
        return userOfCache.get(new UserTypeKey(userId, productType), k ->
                delegate.userOf(k.userId(), k.productType())
        );
    }

    @Override
    public boolean activeUserOf(UUID userId, String productType, int minCount) {
        return activeUserCache.get(new UserTypeKey(userId, productType), k ->
                delegate.activeUserOf(k.userId(), k.productType(), minCount)
        );
    }

    @Override
    public BigDecimal sumByProductAndTxnKind(UUID userId, String productType, String txnKind) {
        return sumCache.get(new UserTypeKindKey(userId, productType, txnKind.toUpperCase()), k ->
                delegate.sumByProductAndTxnKind(k.userId(), k.productType(), k.txnKind())
        );
    }

    @Override public BigDecimal depositSum(UUID u, String t)  { return sumByProductAndTxnKind(u, t, "DEPOSIT"); }
    @Override public BigDecimal withdrawSum(UUID u, String t) { return sumByProductAndTxnKind(u, t, "WITHDRAW"); }

    // CacheClearable
    @Override
    public void clearCaches() {
        userOfCache.invalidateAll();
        activeUserCache.invalidateAll();
        sumCache.invalidateAll();
    }

    @Override
    public String name() { return "KnowledgeRepositoryCached"; }

    private record UserTypeKey(UUID userId, String productType) {
        UserTypeKey { Objects.requireNonNull(userId); Objects.requireNonNull(productType); }
    }
    private record UserTypeKindKey(UUID userId, String productType, String txnKind) {
        UserTypeKindKey { Objects.requireNonNull(userId); Objects.requireNonNull(productType); Objects.requireNonNull(txnKind); }
    }
}