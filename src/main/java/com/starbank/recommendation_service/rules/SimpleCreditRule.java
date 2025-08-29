package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.model.UserFinancialData;
import com.starbank.recommendation_service.model.ProductType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SimpleCreditRule implements RecommendationRuleSet {

    @Override
    public Optional<RecommendationDto> apply(UserFinancialData financialData) {
        boolean hasCredit = financialData.hasProductType(ProductType.CREDIT);
        double sumDebit = financialData.getDepositsByType(ProductType.DEBIT);
        double expensesDebit = financialData.getExpensesByType(ProductType.DEBIT);

        if (!hasCredit && sumDebit > expensesDebit && expensesDebit > 100000) {
            return Optional.of(new RecommendationDto(
                    "simple-credit",
                    "Простой кредит",
                    "Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить нужную сумму? ..."
            ));
        }
        return Optional.empty();
    }
}