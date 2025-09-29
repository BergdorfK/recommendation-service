package com.starbank.recommendation_service.dynamic.service;

import com.starbank.recommendation_service.dynamic.model.DynamicRuleStat;
import com.starbank.recommendation_service.dynamic.repository.RuleStatsRepository;
import com.starbank.recommendation_service.dynamic.repository.RuleStatsView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RuleStatsService {

    private final RuleStatsRepository repo;

    public RuleStatsService(RuleStatsRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void increment(UUID ruleId) {
        // Используем String ID для статистики
        repo.increment(ruleId.toString());
    }

    @Transactional(readOnly = true)
    public List<RuleStatsView> getAll() { // Предполагаем, что RuleStatsView уже определен
        return repo.findAllWithRule();
    }

    @Transactional
    public void deleteByRuleId(UUID ruleId) {
        repo.deleteById(ruleId.toString());
    }
}