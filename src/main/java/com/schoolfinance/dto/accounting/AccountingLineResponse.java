package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingDirection;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountingLineResponse(

        UUID id,

        Integer lineNumber,

        String accountCode,

        String accountName,

        AccountingDirection direction,

        BigDecimal amount
) {
}