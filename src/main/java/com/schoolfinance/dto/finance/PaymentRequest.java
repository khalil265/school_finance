package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(

        @NotNull
        UUID studentId,

        @NotNull
        UUID academicYearId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        PaymentMethod paymentMethod,

        String transactionReference,

        String notes
) {
}