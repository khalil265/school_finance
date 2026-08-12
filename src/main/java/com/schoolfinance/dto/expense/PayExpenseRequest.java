package com.schoolfinance.dto.expense;

import com.schoolfinance.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayExpenseRequest(

        @NotNull
        PaymentMethod paymentMethod,

        String paymentReference,

        @NotBlank
        String treasuryAccountCode,

        @NotBlank
        String expenseAccountCode,

        @NotBlank
        String expenseAccountName,

        String notes
) {
}