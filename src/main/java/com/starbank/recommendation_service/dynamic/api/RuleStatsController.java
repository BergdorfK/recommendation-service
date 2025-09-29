package com.starbank.recommendation_service.dynamic.api;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        var list = service.getAll();
        var items = list.stream()
                .map(v -> Map.of(
                        "rule_id", v.getRuleId(),
                        "count",   v.getCount()
                ))
                .toList();
        return Map.of("stats", items);
    }
}