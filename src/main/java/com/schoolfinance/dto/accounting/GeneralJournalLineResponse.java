package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GeneralJournalLineResponse(

        UUID entryId,

        String entryNumber,

        LocalDateTime entryDate,

        String journalCode,

        String description,

        Integer lineNumber,

        String accountCode,

        String accountName,

        AccountingDirection direction,

        BigDecimal debit,

        BigDecimal credit
) {
}