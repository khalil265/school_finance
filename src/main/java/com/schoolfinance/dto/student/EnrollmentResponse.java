package com.schoolfinance.dto.student;

import com.schoolfinance.enums.EnrollmentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record EnrollmentResponse(

        UUID id,

        UUID studentId,

        String studentRegistrationNumber,

        UUID academicYearId,

        String academicYear,

        UUID schoolClassId,

        String schoolClass,

        String level,

        LocalDate enrollmentDate,

        EnrollmentStatus status,

        String notes
) {
}