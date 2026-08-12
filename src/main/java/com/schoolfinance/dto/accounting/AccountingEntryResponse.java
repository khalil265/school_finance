package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingEntryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AccountingEntryResponse(

        UUID id,

        String entryNumber,

        String journalCode,

        LocalDateTime entryDate,

        String description,

        BigDecimal totalDebit,

        BigDecimal totalCredit,

        AccountingEntryStatus status,

        List<AccountingLineResponse> lines
) {
}