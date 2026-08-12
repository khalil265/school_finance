package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.FeeCategory;
import com.schoolfinance.enums.FeeFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FeeStructureResponse(

        UUID id,

        UUID establishmentId,

        UUID academicYearId,

        String academicYear,

        UUID levelId,

        String level,

        UUID feeTypeId,

        String feeTypeCode,

        String feeTypeName,

        FeeCategory category,

        FeeFrequency frequency,

        BigDecimal amount,

        Integer installmentCount,

        LocalDate firstDueDate,

        Integer gracePeriodDays,

        Boolean active
) {
}