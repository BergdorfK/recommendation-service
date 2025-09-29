package com.starbank.recommendation_service.dynamic.api;

import com.starbank.recommendation_service.dynamic.dto.RuleStatDto;
import com.starbank.recommendation_service.dynamic.service.RuleStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        List<RuleStatDto> list = service.getAllWithZeros();
        return ResponseEntity.ok(Map.of("stats", list));
    }
}