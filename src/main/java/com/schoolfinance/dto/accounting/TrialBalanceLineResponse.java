package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingAccountType;

import java.math.BigDecimal;

public record TrialBalanceLineResponse(

        String accountCode,

        String accountName,

        AccountingAccountType accountType,

        BigDecimal totalDebit,

        BigDecimal totalCredit,

        BigDecimal debitBalance,

        BigDecimal creditBalance
) {
}