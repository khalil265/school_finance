package com.schoolfinance.dto.expense;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateExpenseRequest(

        @NotNull
        UUID establishmentId,

        UUID supplierId,

        @NotNull
        UUID expenseCategoryId,

        @NotBlank
        String subject,

        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount
) {
}