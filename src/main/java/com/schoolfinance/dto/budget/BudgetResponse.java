package com.schoolfinance.dto.budget;

import com.schoolfinance.enums.BudgetStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(

        UUID id,

        UUID establishmentId,

        UUID academicYearId,

        String academicYear,

        String code,

        String name,

        String description,

        BigDecimal totalAmount,

        BigDecimal totalCommitted,

        BigDecimal totalConsumed,

        BigDecimal availableAmount,

        BudgetStatus status
) {
}