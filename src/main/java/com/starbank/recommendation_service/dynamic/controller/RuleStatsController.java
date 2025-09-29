package com.starbank.recommendation_service.dynamic.controller;

import com.starbank.recommendation_service.dto.RuleStatDto;
import com.starbank.recommendation_service.dynamic.repository.RuleStatsView;
import com.starbank.recommendation_service.dynamic.service.RuleStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        List<RuleStatsView> allStats = service.getAll();
        List<RuleStatDto> statsList = allStats.stream()
                .map(view -> new RuleStatDto(UUID.fromString(view.getRuleId()), view.getCount())) // Преобразуем String ID в UUID
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of("stats", statsList);
        return ResponseEntity.ok(response);
    }
}