package com.schoolfinance.dto.cash;

import com.schoolfinance.enums.AccountingDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CashMovementResponse(

        UUID accountingEntryLineId,

        UUID accountingEntryId,

        String entryNumber,

        LocalDateTime entryDate,

        String journalCode,

        String description,

        String accountCode,

        AccountingDirection direction,

        BigDecimal inflow,

        BigDecimal outflow,

        BigDecimal runningBalance
) {
}