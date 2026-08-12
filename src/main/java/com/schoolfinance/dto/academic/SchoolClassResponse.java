package com.schoolfinance.dto.academic;

import java.util.UUID;

public record SchoolClassResponse(

        UUID id,

        UUID establishmentId,

        UUID academicYearId,

        String academicYear,

        UUID levelId,

        String level,

        String code,

        String name,

        Integer capacity,

        Boolean active
) {
}