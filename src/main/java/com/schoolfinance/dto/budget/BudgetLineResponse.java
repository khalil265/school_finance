package com.schoolfinance.dto.budget;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetLineResponse(

        UUID id,

        UUID budgetId,

        String code,

        String name,

        String description,

        BigDecimal allocatedAmount,

        BigDecimal committedAmount,

        BigDecimal consumedAmount,

        BigDecimal availableAmount,

        Boolean active
) {
}