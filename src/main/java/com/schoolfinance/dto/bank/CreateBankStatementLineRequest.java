package com.schoolfinance.dto.bank;

import com.schoolfinance.enums.BankStatementDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBankStatementLineRequest(

        @NotNull
        LocalDate transactionDate,

        String bankReference,

        @NotBlank
        String description,

        @NotNull
        BankStatementDirection direction,

        @NotNull
        @Positive
        BigDecimal amount
) {
}