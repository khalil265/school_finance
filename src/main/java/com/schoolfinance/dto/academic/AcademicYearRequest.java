package com.schoolfinance.dto.academic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AcademicYearRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        @Size(max = 50)
        String code,

        @NotBlank
        @Size(max = 100)
        String label,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        Boolean currentYear
) {
}