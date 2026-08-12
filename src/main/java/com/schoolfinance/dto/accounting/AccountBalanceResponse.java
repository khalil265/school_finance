package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingAccountType;

import java.math.BigDecimal;

public record AccountBalanceResponse(

        String accountCode,

        String accountName,

        AccountingAccountType accountType,

        BigDecimal totalDebit,

        BigDecimal totalCredit,

        BigDecimal balance,

        String balanceNature
) {
}