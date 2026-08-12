package com.schoolfinance.dto.expense;

import jakarta.validation.constraints.NotBlank;

public record RejectExpenseRequest(

        @NotBlank
        String reason
) {
}