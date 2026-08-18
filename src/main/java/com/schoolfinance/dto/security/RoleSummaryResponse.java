package com.schoolfinance.dto.security;

import java.util.UUID;

public record RoleSummaryResponse(

        UUID id,

        String code,

        String name
) {
}