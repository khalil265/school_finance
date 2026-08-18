package com.schoolfinance.dto.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(

        UUID id,

        UUID establishmentId,

        String establishmentName,

        String username,

        String email,

        String firstName,

        String lastName,

        String phone,

        Boolean active,

        Boolean locked,

        LocalDateTime lastLoginAt,

        List<RoleSummaryResponse> roles
) {
}