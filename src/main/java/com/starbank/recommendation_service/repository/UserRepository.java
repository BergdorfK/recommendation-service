package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.model.ProductType;
import com.starbank.recommendation_service.model.UserFinancialData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(@Qualifier("h2JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserFinancialData getUserFinancialData(UUID userId) {
        String sql = """
                SELECT 
                    p.type,
                    SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END) AS total_deposits,
                    SUM(CASE WHEN t.amount < 0 THEN -t.amount ELSE 0 END) AS total_expenses,
                    COUNT(DISTINCT p.id) AS product_count
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ?
                GROUP BY p.type
                """;

        UserFinancialData financialData = new UserFinancialData();

        jdbcTemplate.query(
                sql,
                ps -> ps.setObject(1, userId),
                rs -> {
                    String typeStr = rs.getString("type");
                    ProductType type = ProductType.fromString(typeStr);

                    double deposits = rs.getDouble("total_deposits");
                    double expenses = rs.getDouble("total_expenses");
                    int productCount = rs.getInt("product_count");

                    financialData.setDepositsByType(type, deposits);
                    financialData.setExpensesByType(type, expenses);
                    financialData.setProductCountByType(type, productCount);
                }
        );

        return financialData;
    }
}