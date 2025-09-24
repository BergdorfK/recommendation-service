package com.starbank.recommendation_service.dynamic.api;

import com.starbank.recommendation_service.dynamic.repository.RuleStatsView;
import com.starbank.recommendation_service.dynamic.service.RuleStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public List<RuleStatsView> stats() {
        return service.getAll();
    }
}