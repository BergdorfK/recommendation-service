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

    // Все правила, даже если не было ни одного срабатывания (count = 0)
    @Query(value = """
        SELECT r.id AS ruleId,
               COALESCE(s.count, 0) AS count
          FROM dynamic_rule r
          LEFT JOIN dynamic_rule_stats s ON s.rule_id = r.id
         ORDER BY r.created_at NULLS LAST, r.product_name
        """, nativeQuery = true)
    List<ShortView> allCountsForAllRules();

    // Оставляем для совместимости с существующим кодом/просмотром (если где-то используется)
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