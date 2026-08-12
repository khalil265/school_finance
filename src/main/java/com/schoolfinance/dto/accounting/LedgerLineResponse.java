package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerLineResponse(

        UUID entryId,

        String entryNumber,

        LocalDateTime entryDate,

        String journalCode,

        String description,

        AccountingDirection direction,

        BigDecimal debit,

        BigDecimal credit,

        BigDecimal runningBalance
) {
}