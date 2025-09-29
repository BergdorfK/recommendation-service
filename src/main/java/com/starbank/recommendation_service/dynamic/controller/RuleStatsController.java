import com.fasterxml.jackson.annotation.JsonProperty;
import com.starbank.recommendation_service.dynamic.service.RuleStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatsService service;

    public RuleStatsController(RuleStatsService service) {
        this.service = service;
    }

    record StatItem(@JsonProperty("rule_id") UUID ruleId,
                    @JsonProperty("count") long count) {}

    record StatsResponse(@JsonProperty("stats") List<StatItem> stats) {}

    @GetMapping("/stats")
    public StatsResponse stats() {
        var list = service.getAll();
        var res = list.stream()
                .map(v -> new StatItem(v.getRuleId(), v.getCount()))
                .toList();
        return new StatsResponse(res);
    }
}