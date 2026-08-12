package com.schoolfinance.dto.student;

import com.schoolfinance.enums.Gender;
import com.schoolfinance.enums.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudentResponse(

        UUID id,

        UUID establishmentId,

        String establishmentName,

        String registrationNumber,

        String firstName,

        String lastName,

        Gender gender,

        LocalDate dateOfBirth,

        String placeOfBirth,

        String nationality,

        String phone,

        String email,

        String address,

        String guardianName,

        String guardianPhone,

        String guardianEmail,

        StudentStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}