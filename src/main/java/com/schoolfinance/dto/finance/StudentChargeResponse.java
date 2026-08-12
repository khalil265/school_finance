package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.ChargeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record StudentChargeResponse(

        UUID id,

        UUID studentAccountId,

        UUID feeStructureId,

        UUID feeTypeId,

        String feeTypeCode,

        String feeTypeName,

        Integer installmentNumber,

        String label,

        BigDecimal amount,

        BigDecimal paidAmount,

        BigDecimal discountAmount,

        BigDecimal remainingAmount,

        LocalDate dueDate,

        Integer gracePeriodDays,

        ChargeStatus status
) {
}