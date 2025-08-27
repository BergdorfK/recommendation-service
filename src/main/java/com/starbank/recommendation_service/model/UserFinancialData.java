package com.starbank.recommendation_service.model;

import java.util.HashMap;
import java.util.Map;

public class UserFinancialData {
    private Map<ProductType, Double> depositsByType = new HashMap<>();
    private Map<ProductType, Double> expensesByType = new HashMap<>();
    private Map<ProductType, Integer> productCountByType = new HashMap<>();

    public UserFinancialData() {
        for (ProductType type : ProductType.values()) {
            depositsByType.put(type, 0.0);
            expensesByType.put(type, 0.0);
            productCountByType.put(type, 0);
        }
    }

    public boolean hasProductType(ProductType type) {
        return productCountByType.getOrDefault(type, 0) > 0;
    }

    public double getDepositsByType(ProductType type) {
        return depositsByType.getOrDefault(type, 0.0);
    }

    public double getExpensesByType(ProductType type) {
        return expensesByType.getOrDefault(type, 0.0);
    }

    public int getProductCountByType(ProductType type) {
        return productCountByType.getOrDefault(type, 0);
    }

    public void setDepositsByType(ProductType type, double amount) {
        depositsByType.put(type, amount);
    }

    public void setExpensesByType(ProductType type, double amount) {
        expensesByType.put(type, amount);
    }

    public void setProductCountByType(ProductType type, int count) {
        productCountByType.put(type, count);
    }
}