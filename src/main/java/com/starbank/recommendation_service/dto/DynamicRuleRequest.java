package com.starbank.recommendation_service.dto;

import java.util.List;

/**
 * DTO для создания динамического правила рекомендации
 */
public class DynamicRuleRequest {

    /**
     * Название продукта, который рекомендуется
     */
    private String product_name;

    /**
     * ID продукта, который рекомендуется
     */
    private String product_id;

    /**
     * Текст рекомендации
     */
    private String product_text;

    /**
     * Список условий правила
     */
    private List<RuleCondition> rule;

    // Конструкторы
    public DynamicRuleRequest() {
    }

    public DynamicRuleRequest(String product_name, String product_id, String product_text, List<RuleCondition> rule) {
        this.product_name = product_name;
        this.product_id = product_id;
        this.product_text = product_text;
        this.rule = rule;
    }

    // Геттеры и сеттеры
    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProductId(String product_id) {
        this.product_id = product_id;
    }

    public String getProductText() {
        return product_text;
    }

    public void setProductText(String product_text) {
        this.product_text = product_text;
    }

    public List<RuleCondition> getRule() {
        return rule;
    }

    public void setRule(List<RuleCondition> rule) {
        this.rule = rule;
    }

    /**
     * Класс для представления одного условия в правиле
     */
    public static class RuleCondition {

        /**
         * Тип запроса (USER_OF, ACTIVE_USER_OF, TRANSACTION_SUM_COMPARE, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW)
         */
        private String query;

        /**
         * Аргументы запроса
         */
        private List<String> arguments;

        /**
         * Модификатор отрицания (true - отрицание, false - без отрицания)
         */
        private boolean negate;

        // Конструкторы
        public RuleCondition() {
        }

        public RuleCondition(String query, List<String> arguments, boolean negate) {
            this.query = query;
            this.arguments = arguments;
            this.negate = negate;
        }

        // Геттеры и сеттеры
        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public List<String> getArguments() {
            return arguments;
        }

        public void setArguments(List<String> arguments) {
            this.arguments = arguments;
        }

        public boolean isNegate() {
            return negate;
        }

        public void setNegate(boolean negate) {
            this.negate = negate;
        }

        @Override
        public String toString() {
            return "RuleCondition{" +
                    "query='" + query + '\'' +
                    ", arguments=" + arguments +
                    ", negate=" + negate +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DynamicRuleRequest{" +
                "product_name='" + product_name + '\'' +
                ", product_id='" + product_id + '\'' +
                ", product_text='" + product_text + '\'' +
                ", rule=" + rule +
                '}';
    }
}