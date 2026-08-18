package com.schoolfinance.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record UserCreateRequest(

        UUID establishmentId,

        @NotBlank
        @Size(max = 100)
        String username,

        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        String phone,

        Set<UUID> roleIds
) {
}