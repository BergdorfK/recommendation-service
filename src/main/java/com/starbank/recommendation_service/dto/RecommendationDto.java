package com.starbank.recommendation_service.dto;

public class RecommendationDto {

    private String id;
    private String name;
    private String text;

    public RecommendationDto(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecommendationDto that)) return false;
        return id.equals(that.id) && name.equals(that.name) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, text);
    }
}