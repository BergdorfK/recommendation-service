package com.starbank.recommendation_service.dynamic.repository;

import com.starbank.recommendation_service.dynamic.model.DynamicRuleStat;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface RuleStatsRepository extends JpaRepository<DynamicRuleStat, UUID> {

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO dynamic_rule_stats(rule_id, count)
        VALUES (:ruleId, 1)
        ON CONFLICT (rule_id) DO UPDATE
          SET count = dynamic_rule_stats.count + 1
        """, nativeQuery = true)
    void increment(@Param("ruleId") UUID ruleId);

    @Query(value = """
        SELECT s.rule_id AS ruleId,
               s.count   AS count,
               r.product_code AS productCode,
               r.product_name AS productName
          FROM dynamic_rule_stats s
          JOIN dynamic_rule r ON r.id = s.rule_id
         ORDER BY s.count DESC, r.product_name
        """, nativeQuery = true)
    List<RuleStatsView> findAllWithRule();
}