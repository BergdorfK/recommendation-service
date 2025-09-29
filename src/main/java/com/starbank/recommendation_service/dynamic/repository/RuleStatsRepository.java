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

    interface ShortView {
        UUID getRuleId();
        long getCount();
    }

    @Query(value = "SELECT rule_id AS ruleId, count AS count FROM dynamic_rule_stats", nativeQuery = true)
    List<ShortView> allCounts();
}