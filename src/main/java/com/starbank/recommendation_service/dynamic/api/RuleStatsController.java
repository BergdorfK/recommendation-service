package com.starbank.recommendation_service.dynamic.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starbank.recommendation_service.dynamic.repository.RuleStatsRepository;
import com.starbank.recommendation_service.dynamic.service.RuleStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<RuleStatsRepository.ShortView> rows = service.allCountsAllRules();

        List<StatsItem> items = rows.stream()
                .map(r -> new StatsItem(r.getRuleId(), r.getCount()))
                .collect(Collectors.toList());

        return Map.of("stats", items);
    }

    public static final class StatsItem {
        @JsonProperty("rule_id")
        private final UUID ruleId;
        @JsonProperty("count")
        private final String count;

        public StatsItem(UUID ruleId, long count) {
            this.ruleId = ruleId;
            this.count = Long.toString(count);
        }

        public UUID getRuleId() { return ruleId; }
        public String getCount() { return count; }
    }
}