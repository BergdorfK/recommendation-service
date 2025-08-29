package com.starbank.recommendation_service.model;

public enum ProductType {
    DEBIT("DEBIT"),
    CREDIT("CREDIT"),
    INVEST("INVEST"),
    SAVING("SAVING");

    private final String dbValue;

    ProductType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ProductType fromString(String value) {
        for (ProductType type : ProductType.values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown product type: " + value);
    }
}