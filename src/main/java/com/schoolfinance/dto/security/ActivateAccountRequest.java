package com.schoolfinance.dto.security;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ActivateAccountRequest(

        @NotNull
        UUID token,

        @NotBlank
        @Size(min = 8)
        String password
) {
}