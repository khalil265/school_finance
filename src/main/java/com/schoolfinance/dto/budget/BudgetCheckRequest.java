package com.schoolfinance.dto.budget;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BudgetCheckRequest(

        @NotNull
        UUID budgetLineId
) {
}