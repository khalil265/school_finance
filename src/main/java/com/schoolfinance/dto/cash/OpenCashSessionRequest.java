package com.schoolfinance.dto.cash;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record OpenCashSessionRequest(

        @NotNull
        UUID establishmentId,

        String accountCode,

        @NotNull
        @PositiveOrZero
        BigDecimal openingBalance
) {
}