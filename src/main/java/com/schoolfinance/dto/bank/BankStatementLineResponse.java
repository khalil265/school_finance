package com.schoolfinance.dto.bank;

import com.schoolfinance.enums.BankStatementDirection;
import com.schoolfinance.enums.BankStatementLineStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BankStatementLineResponse(

        UUID id,

        UUID bankStatementId,

        LocalDate transactionDate,

        String bankReference,

        String description,

        BankStatementDirection direction,

        BigDecimal amount,

        BankStatementLineStatus status,

        UUID accountingEntryLineId,

        String accountingEntryNumber,

        String accountCode,

        BigDecimal accountingAmount,

        BigDecimal differenceAmount,

        LocalDateTime reconciledAt,

        String reconciledBy
) {
}