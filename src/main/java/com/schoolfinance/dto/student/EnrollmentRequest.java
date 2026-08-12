package com.schoolfinance.dto.student;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EnrollmentRequest(

        @NotNull
        UUID academicYearId,

        @NotNull
        UUID schoolClassId,

        LocalDate enrollmentDate,

        String notes
) {
}