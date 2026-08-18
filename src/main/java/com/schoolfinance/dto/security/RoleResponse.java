package com.schoolfinance.dto.security;

import java.util.List;
import java.util.UUID;

public record RoleResponse(

        UUID id,

        String code,

        String name,

        String description,

        Boolean systemRole,

        Boolean active,

        List<PermissionResponse> permissions
) {
}