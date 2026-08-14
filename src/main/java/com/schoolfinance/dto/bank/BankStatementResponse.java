package com.schoolfinance.dto.bank;

import com.schoolfinance.enums.BankStatementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BankStatementResponse(

        UUID id,

        UUID establishmentId,

        String statementReference,

        String bankName,

        String bankAccountNumber,

        String accountCode,

        LocalDate startDate,

        LocalDate endDate,

        BigDecimal openingBalance,

        BigDecimal closingBalance,

        BankStatementStatus status
) {
}