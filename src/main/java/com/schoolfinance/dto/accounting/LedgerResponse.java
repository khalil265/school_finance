package com.schoolfinance.dto.accounting;

import java.math.BigDecimal;
import java.util.List;

public record LedgerResponse(

        String accountCode,

        String accountName,

        BigDecimal totalDebit,

        BigDecimal totalCredit,

        BigDecimal balance,

        String balanceNature,

        List<LedgerLineResponse> lines
) {
}