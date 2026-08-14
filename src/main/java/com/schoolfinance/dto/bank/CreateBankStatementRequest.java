package com.schoolfinance.dto.bank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBankStatementRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        String statementReference,

        @NotBlank
        String bankName,

        String bankAccountNumber,

        @NotBlank
        String accountCode,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotNull
        BigDecimal openingBalance,

        @NotNull
        BigDecimal closingBalance
) {
}