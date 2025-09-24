package com.starbank.recommendation_service.dynamic.eval;

import com.starbank.recommendation_service.dynamic.dto.RuleQueryDto;
import com.starbank.recommendation_service.knowledge.KnowledgeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RuleEvaluator {

    private final KnowledgeRepository knowledge;

    public RuleEvaluator(KnowledgeRepository knowledge) {
        this.knowledge = knowledge;
    }

    public boolean matches(UUID userId, List<RuleQueryDto> queries) {
        if (queries == null || queries.isEmpty()) return false;
        for (RuleQueryDto q : queries) {
            boolean ok = switch (q.query()) {
                case "USER_OF" -> {
                    String productType = arg(q, 0);
                    yield knowledge.userOf(userId, productType);
                }
                case "ACTIVE_USER_OF" -> {
                    String productType = arg(q, 0);
                    yield knowledge.activeUserOf(userId, productType, 5);
                }
                case "TRANSACTION_SUM_COMPARE" -> {
                    String productType = arg(q, 0);
                    String txnKind    = arg(q, 1); // DEPOSIT | WITHDRAW
                    String op         = arg(q, 2); // >, <, =, >=, <=
                    int threshold     = Integer.parseInt(arg(q, 3));
                    BigDecimal left   = knowledge.sumByProductAndTxnKind(userId, productType, txnKind);
                    BigDecimal right  = new BigDecimal(threshold);
                    yield cmp(left, right, op);
                }
                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                    String productType = arg(q, 0);
                    String op          = arg(q, 1);
                    BigDecimal deposit = knowledge.depositSum(userId, productType);
                    BigDecimal withd   = knowledge.withdrawSum(userId, productType);
                    yield cmp(deposit, withd, op);
                }
                default -> false;
            };
            if (q.negate()) ok = !ok;
            if (!ok) return false;
        }
        return true;
    }

    private static String arg(RuleQueryDto q, int idx) {
        if (q.arguments() == null || q.arguments().size() <= idx)
            throw new IllegalArgumentException("Bad rule arguments for "+q.query());
        return q.arguments().get(idx);
    }

    private static boolean cmp(BigDecimal a, BigDecimal b, String op) {
        int c = a.compareTo(b);
        return switch (op) {
            case ">"  -> c > 0;
            case "<"  -> c < 0;
            case "="  -> c == 0;
            case ">=" -> c >= 0;
            case "<=" -> c <= 0;
            default   -> false;
        };
    }
}
