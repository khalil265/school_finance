package com.schoolfinance.dto.expense;

import java.util.UUID;

public record ExpenseCategoryResponse(

        UUID id,

        UUID establishmentId,

        String code,

        String name,

        String description,

        Boolean active
) {
}