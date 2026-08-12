package com.schoolfinance.dto.student;

import com.schoolfinance.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record StudentCreateRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        @Size(max = 50)
        String registrationNumber,

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotNull
        Gender gender,

        LocalDate dateOfBirth,

        @Size(max = 150)
        String placeOfBirth,

        @Size(max = 100)
        String nationality,

        @Size(max = 50)
        String phone,

        @Email
        @Size(max = 150)
        String email,

        String address,

        @Size(max = 200)
        String guardianName,

        @Size(max = 50)
        String guardianPhone,

        @Email
        @Size(max = 150)
        String guardianEmail
) {
}