package com.starbank.recommendation_service.dynamic.service;

import com.starbank.recommendation_service.dynamic.dto.RuleStatDto;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import com.starbank.recommendation_service.dynamic.repository.DynamicRuleRepository;
import com.starbank.recommendation_service.dynamic.repository.RuleStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RuleStatsService {

    private final RuleStatsRepository repo;
    private final DynamicRuleRepository rules;

    public RuleStatsService(RuleStatsRepository repo, DynamicRuleRepository rules) {
        this.repo = repo;
        this.rules = rules;
    }

    @Transactional(transactionManager = "rulesTransactionManager")
    public void increment(UUID ruleId) {
        repo.increment(ruleId);
    }

    @Transactional(readOnly = true, transactionManager = "rulesTransactionManager")
    public List<RuleStatDto> getAllWithZeros() {
        Map<UUID, Long> counts = new HashMap<>();
        for (RuleStatsRepository.ShortView v : repo.allCounts()) {
            counts.put(v.getRuleId(), v.getCount());
        }
        List<RuleStatDto> out = new ArrayList<>();
        for (DynamicRule r : rules.findAll()) {
            long c = counts.getOrDefault(r.getId(), 0L);
            out.add(new RuleStatDto(r.getId(), c));
        }
        // сортировка: по count desc, затем по UUID
        out.sort(Comparator.<RuleStatDto>comparingLong(RuleStatDto::count).reversed()
                .thenComparing(RuleStatDto::rule_id));
        return out;
    }
}