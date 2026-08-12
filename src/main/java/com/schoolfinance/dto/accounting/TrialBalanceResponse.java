package com.schoolfinance.dto.accounting;

import java.math.BigDecimal;
import java.util.List;

public record TrialBalanceResponse(

        BigDecimal totalDebit,

        BigDecimal totalCredit,

        BigDecimal totalDebitBalance,

        BigDecimal totalCreditBalance,

        boolean balanced,

        List<TrialBalanceLineResponse> accounts
) {
}