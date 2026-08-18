package com.schoolfinance.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record RoleCreateRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        String description,

        Set<UUID> permissionIds
) {
}