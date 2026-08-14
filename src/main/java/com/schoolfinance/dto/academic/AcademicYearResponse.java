package com.schoolfinance.dto.academic;

import com.schoolfinance.enums.AcademicYearStatus;

import java.time.LocalDate;
import java.util.UUID;

public record AcademicYearResponse(

        UUID id,

        UUID establishmentId,

        String code,

        String label,

        LocalDate startDate,

        LocalDate endDate,

        AcademicYearStatus status,

        Boolean currentYear
) {
}