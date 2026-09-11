package com.schoolfinance.dto.student;

import com.schoolfinance.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record StudentCreateRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        String registrationNumber,

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

        String photoBase64
) {
}