package com.schoolfinance.dto.academic;

import java.util.UUID;

public record LevelResponse(

        UUID id,

        UUID establishmentId,

        String code,

        String name,

        String description,

        Integer displayOrder,

        Boolean active
) {
}