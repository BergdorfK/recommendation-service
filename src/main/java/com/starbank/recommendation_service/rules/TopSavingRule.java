package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.model.UserFinancialData;
import com.starbank.recommendation_service.model.ProductType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TopSavingRule implements RecommendationRuleSet {

    @Override
    public Optional<RecommendationDto> apply(UserFinancialData financialData) {
        boolean hasDebit = financialData.hasProductType(ProductType.DEBIT);
        double sumDebit = financialData.getDepositsByType(ProductType.DEBIT);
        double sumSaving = financialData.getDepositsByType(ProductType.SAVING);
        double expensesDebit = financialData.getExpensesByType(ProductType.DEBIT);

        if (hasDebit &&
                (sumDebit >= 50000 || sumSaving >= 50000) &&
                sumDebit > expensesDebit) {
            return Optional.of(new RecommendationDto(
                    "top-saving",
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент..."
            ));
        }
        return Optional.empty();
    }
}