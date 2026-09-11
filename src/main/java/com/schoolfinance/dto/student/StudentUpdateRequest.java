package com.schoolfinance.dto.student;

import com.schoolfinance.enums.Gender;
import com.schoolfinance.enums.StudentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentUpdateRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
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

        @NotNull
        StudentStatus status,

        String photoBase64
) {
}