package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAccountingAccountRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        @Size(max = 30)
        String code,

        @NotBlank
        @Size(max = 200)
        String name,

        @NotNull
        AccountingAccountType accountType,

        UUID parentId,

        String description,

        Boolean postingAllowed
) {
}