package com.schoolfinance.dto.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FeeStructureRequest(

        @NotNull
        UUID establishmentId,

        @NotNull
        UUID academicYearId,

        @NotNull
        UUID levelId,

        @NotNull
        UUID feeTypeId,

        @NotNull
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal amount,

        @Min(1)
        Integer installmentCount,

        LocalDate firstDueDate,

        @Min(0)
        Integer gracePeriodDays
) {
}