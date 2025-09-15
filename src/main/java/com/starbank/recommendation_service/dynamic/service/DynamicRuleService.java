package com.starbank.recommendation_service.dynamic.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendation_service.dynamic.model.DynamicRule;
import com.starbank.recommendation_service.dynamic.repository.DynamicRuleRepository;
import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.model.UserFinancialData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public DynamicRuleService(DynamicRuleRepository repository) {
        this.repository = repository;
    }

    //Пока применяем «всем подходит», позже подключить разбор выражения when.
    public List<RecommendationDto> evaluateDynamic(UserFinancialData data) {
        List<RecommendationDto> out = new ArrayList<>();
        for (DynamicRule r : repository.findAll()) {
            if (matches(r, data)) {
                RecommendationDto dto = mapToDto(r);
                if (dto != null && notBlank(dto.getId())) {
                    out.add(dto);
                }
            }
        }
        return out;
    }

    private boolean matches(DynamicRule r, UserFinancialData data) {
        String json = r.getRuleJson();
        if (json == null || json.isBlank()) return true;
        try {
            JsonNode node = mapper.readTree(json);
            if (node.hasNonNull("enabled") && !node.get("enabled").asBoolean(true)) {
                return false;
            }
            // TODO: здесь позже будет "when" и реально проверим условия
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private RecommendationDto mapToDto(DynamicRule r) {
        String id = null;

        if (r.getProductCode() != null && !r.getProductCode().isBlank()) {
            id = r.getProductCode();
        }

        if (id == null && r.getRuleJson() != null && !r.getRuleJson().isBlank()) {
            try {
                JsonNode node = mapper.readTree(r.getRuleJson());
                if (node.hasNonNull("id")) {
                    id = node.get("id").asText();
                }
            } catch (Exception ignored) {
            }
        }

        if (id == null && r.getProductId() != null) {
            id = r.getProductId().toString();
        }

        if (id == null) return null;

        return new RecommendationDto(id, r.getProductName(), r.getProductText());
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}