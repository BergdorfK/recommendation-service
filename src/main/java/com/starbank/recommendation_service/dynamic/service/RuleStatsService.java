package com.starbank.recommendation_service.dynamic.service;

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
        repo.increment(ruleId);
    }

    @Transactional(readOnly = true)
    public List<RuleStatsRepository.ShortView> allCountsAllRules() {
        return repo.allCountsForAllRules();
    }

    @Transactional(readOnly = true)
    public List<RuleStatsView> getAll() {
        return repo.findAllWithRule();
    }
}