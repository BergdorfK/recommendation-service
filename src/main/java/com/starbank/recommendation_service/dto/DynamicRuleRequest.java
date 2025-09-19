package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DynamicRuleRequest {

    @JsonProperty("product_name")
    private String product_name;
    @JsonProperty("product_id")
    private String product_id;
    @JsonProperty("product_text")
    private String product_text;
    @JsonProperty("rule")
    private List<RuleCondition> rule;

    public DynamicRuleRequest() {}

    public DynamicRuleRequest(String product_name, String product_id, String product_text, List<RuleCondition> rule) {
        this.product_name = product_name;
        this.product_id = product_id;
        this.product_text = product_text;
        this.rule = rule;
    }

    public String getProductName() { return product_name; }
    public void setProductName(String product_name) { this.product_name = product_name; }
    public String getProductId() { return product_id; }
    public void setProductId(String product_id) { this.product_id = product_id; }
    public String getProductText() { return product_text; }
    public void setProductText(String product_text) { this.product_text = product_text; }
    public List<RuleCondition> getRule() { return rule; }
    public void setRule(List<RuleCondition> rule) { this.rule = rule; }

    public static class RuleCondition {
        @JsonProperty("query")
        private String query;
        @JsonProperty("arguments")
        private List<String> arguments;
        @JsonProperty("negate")
        private boolean negate;

        public RuleCondition() {}
        public RuleCondition(String query, List<String> arguments, boolean negate) {
            this.query = query;
            this.arguments = arguments;
            this.negate = negate;
        }

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public List<String> getArguments() { return arguments; }
        public void setArguments(List<String> arguments) { this.arguments = arguments; }
        public boolean isNegate() { return negate; }
        public void setNegate(boolean negate) { this.negate = negate; }

        @Override public String toString() {
            return "RuleCondition{" +
                    "query='" + query + '\'' +
                    ", arguments=" + arguments +
                    ", negate=" + negate +
                    '}';
        }
    }

    @Override public String toString() {
        return "DynamicRuleRequest{" +
                "product_name='" + product_name + '\'' +
                ", product_id='" + product_id + '\'' +
                ", product_text='" + product_text + '\'' +
                ", rule=" + rule +
                '}';
    }
}