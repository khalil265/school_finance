package com.schoolfinance.dto.accounting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountingAccountRequest(

        @NotBlank
        @Size(max = 200)
        String name,

        String description,

        Boolean postingAllowed,

        Boolean active
) {
}