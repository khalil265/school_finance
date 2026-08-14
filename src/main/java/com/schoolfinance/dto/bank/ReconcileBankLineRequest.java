package com.schoolfinance.dto.bank;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReconcileBankLineRequest(

        @NotNull
        UUID accountingEntryLineId
) {
}