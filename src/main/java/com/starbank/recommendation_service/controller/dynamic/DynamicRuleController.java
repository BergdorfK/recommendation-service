package com.starbank.recommendation_service.controller.dynamic;

import com.starbank.recommendation_service.dto.DynamicRuleRequest;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import com.starbank.recommendation_service.dynamic.service.DynamicRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
@CrossOrigin(origins = "*")
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    /**
     * Создание нового динамического правила рекомендаций
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRule(@RequestBody DynamicRuleRequest request) {
        try {
            // Валидация входных данных
            if (request.getProductId() == null || request.getProductId().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Product ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            DynamicRule rule = new DynamicRule();
            rule.setProductId(UUID.fromString(request.getProductId()));
            rule.setProductName(request.getProductName());
            rule.setProductText(request.getProductText());
            rule.setRuleJson(dynamicRuleService.convertRuleToJson(request.getRule()));

            DynamicRule savedRule = dynamicRuleService.saveRule(rule);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedRule.getId() != null ? savedRule.getId().toString() : "");
            response.put("product_name", savedRule.getProductName());
            response.put("product_id", savedRule.getProductId().toString());
            response.put("product_text", savedRule.getProductText());
            response.put("rule", request.getRule() != null ? request.getRule() : new ArrayList<>());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create rule: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Получение списка всех динамических правил
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRules() {
        try {
            List<DynamicRule> rules = dynamicRuleService.getAllRules();
            List<Map<String, Object>> responseData = new ArrayList<>();

            for (DynamicRule rule : rules) {
                Map<String, Object> ruleMap = new HashMap<>();
                ruleMap.put("id", rule.getId() != null ? rule.getId().toString() : "");
                ruleMap.put("product_name", rule.getProductName() != null ? rule.getProductName() : "");
                ruleMap.put("product_id", rule.getProductId() != null ? rule.getProductId().toString() : "");
                ruleMap.put("product_text", rule.getProductText() != null ? rule.getProductText() : "");
                try {
                    List<DynamicRuleRequest.RuleCondition> ruleConditions =
                            rule.getRuleJson() != null ? dynamicRuleService.convertJsonToRule(rule.getRuleJson()) : new ArrayList<>();
                    ruleMap.put("rule", ruleConditions);
                } catch (Exception e) {
                    ruleMap.put("rule", new ArrayList<>());
                }
                responseData.add(ruleMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", responseData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve rules: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Удаление динамического правила по ID продукта
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> deleteRule(@PathVariable String productId) {
        try {
            if (productId == null || productId.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Product ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            UUID productUuid = UUID.fromString(productId);
            dynamicRuleService.deleteRuleByProductId(productUuid);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Rule not found");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete rule: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}