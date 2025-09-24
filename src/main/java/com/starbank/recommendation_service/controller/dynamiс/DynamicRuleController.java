package com.starbank.recommendation_service.controller.dynamic;

import com.starbank.recommendation_service.dto.DynamicRuleRequest;
import com.starbank.recommendation_service.dynamic.mapper.DynamicRuleMapper;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import com.starbank.recommendation_service.dynamic.service.DynamicRuleService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rule")
@CrossOrigin(origins = "*")
public class DynamicRuleController {

    private final DynamicRuleService service;

    public DynamicRuleController(DynamicRuleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRule(@RequestBody DynamicRuleRequest request) {
        if (request.getProductId() == null || request.getProductId().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "product_id is required"));

        DynamicRule saved = service.create(request);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", saved.getId());
        resp.put("product_name", saved.getProductName());
        resp.put("product_id", saved.getProductId());
        resp.put("product_text", saved.getProductText());
        resp.put("rule", request.getRule());
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listRules() {
        var list = service.listAll();
        List<Map<String, Object>> data = new ArrayList<>();
        for (var r : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("product_name", r.getProductName());
            item.put("product_id", r.getProductId());
            item.put("product_text", r.getProductText());
            item.put("rule", DynamicRuleMapper.read(r.getRuleJson()));
            data.add(item);
        }
        return ResponseEntity.ok(Map.of("data", data));
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteByProduct(@PathVariable UUID productId) {
        boolean deleted = service.deleteByProductId(productId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}