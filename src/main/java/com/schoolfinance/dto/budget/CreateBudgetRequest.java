package com.schoolfinance.dto.budget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBudgetRequest(

        @NotNull
        UUID establishmentId,

        @NotNull
        UUID academicYearId,

        @NotBlank
        String code,

        @NotBlank
        String name,

        String description
) {
}