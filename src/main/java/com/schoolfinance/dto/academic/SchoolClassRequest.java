package com.schoolfinance.dto.academic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SchoolClassRequest(

        @NotNull
        UUID establishmentId,

        @NotNull
        UUID academicYearId,

        @NotNull
        UUID levelId,

        @NotBlank
        @Size(max = 50)
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        @Positive
        Integer capacity
) {
}