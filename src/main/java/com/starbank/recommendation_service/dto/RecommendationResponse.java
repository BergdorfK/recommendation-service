package com.starbank.recommendation_service.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResponse {
    private String userId;
    private List<RecommendationDto> recommendations = new ArrayList<>();

    public RecommendationResponse() {
    }

    public RecommendationResponse(String userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
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