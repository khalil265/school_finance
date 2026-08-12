package com.schoolfinance.dto.audit;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(

        UUID id,

        String username,

        String action,

        String entityType,

        String entityId,

        String oldValue,

        String newValue,

        String ipAddress,

        String userAgent,

        LocalDateTime createdAt
) {
}