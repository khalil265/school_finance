package com.schoolfinance.dto.expense;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SupplierRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        String code,

        @NotBlank
        String name,

        String taxIdentifier,

        String phone,

        @Email
        String email,

        String address,

        String bankName,

        String bankAccount
) {
}