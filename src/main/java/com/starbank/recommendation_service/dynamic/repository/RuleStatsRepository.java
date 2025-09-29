package com.starbank.recommendation_service.dynamic.repository;

import com.starbank.recommendation_service.dynamic.model.DynamicRuleStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleStatsRepository extends JpaRepository<DynamicRuleStat, String> { // ID правила как строка

    // Увеличить счетчик
    @Modifying
    @Query(value = "INSERT INTO dynamic_rule_stats (rule_id, count) VALUES (?1, 1) ON CONFLICT (rule_id) DO UPDATE SET count = dynamic_rule_stats.count + 1", nativeQuery = true)
    void increment(String ruleId); // Используем native query для UPSERT

    // Найти все статистики с информацией о правилах (JOIN)
    @Query(value = """
        SELECT s.rule_id AS ruleId, s.count AS count, r.product_code AS productCode, r.product_name AS productName
        FROM dynamic_rule_stats s
        LEFT JOIN dynamic_rule r ON r.id = s.rule_id -- LEFT JOIN, чтобы включить статистику без правила, если нужно
        ORDER BY s.count DESC, r.product_name
        """, nativeQuery = true)
    List<RuleStatsView> findAllWithRule();

    // Найти статистику по ID правила
    Optional<DynamicRuleStat> findByRuleId(String ruleId);
}