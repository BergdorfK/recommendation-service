package com.starbank.recommendation_service.rules;

import com.starbank.recommendation_service.dto.RecommendationDto;
import com.starbank.recommendation_service.model.UserFinancialData;
import com.starbank.recommendation_service.model.ProductType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Invest500Rule implements RecommendationRuleSet {

    @Override
    public Optional<RecommendationDto> apply(UserFinancialData financialData) {
        boolean hasDebit = financialData.hasProductType(ProductType.DEBIT);
        boolean hasInvest = financialData.hasProductType(ProductType.INVEST);
        double sumSaving = financialData.getDepositsByType(ProductType.SAVING);

        if (hasDebit && !hasInvest && sumSaving > 1000) {
            return Optional.of(new RecommendationDto(
                    "invest-500",
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом..."
            ));
        }
        return Optional.empty();
    }
}