package com.starbank.recommendation_service.dynamic.repository;

import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    Optional<DynamicRule> findByProductCode(String productId);
    Optional<DynamicRule> findByProductId(UUID productId);

}