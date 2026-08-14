package com.schoolfinance.dto.bank;

import com.schoolfinance.enums.AccountingDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BankReconciliationCandidateResponse(

        UUID accountingEntryLineId,

        UUID accountingEntryId,

        String entryNumber,

        LocalDateTime entryDate,

        String accountCode,

        String accountName,

        AccountingDirection direction,

        BigDecimal amount,

        String description
) {
}