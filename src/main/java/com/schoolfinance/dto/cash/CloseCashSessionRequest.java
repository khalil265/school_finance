package com.schoolfinance.dto.cash;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CloseCashSessionRequest(

        @NotNull
        @PositiveOrZero
        BigDecimal physicalBalance,

        String notes
) {
}