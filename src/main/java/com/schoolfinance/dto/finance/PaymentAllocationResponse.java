package com.schoolfinance.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentAllocationResponse(

        UUID allocationId,

        UUID chargeId,

        String feeTypeCode,

        String chargeLabel,

        LocalDate dueDate,

        BigDecimal allocatedAmount,

        BigDecimal remainingAmount
) {
}