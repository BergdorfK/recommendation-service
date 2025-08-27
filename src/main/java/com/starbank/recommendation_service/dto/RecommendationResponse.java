package com.starbank.recommendation_service.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResponse {
    private String userId;
    private List<RecommendationDto> recommendations = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public List<RecommendationDto> getRecommendations() {
        return recommendations;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRecommendations(List<RecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }
}