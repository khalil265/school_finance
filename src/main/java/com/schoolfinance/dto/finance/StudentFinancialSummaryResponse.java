package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.StudentAccountStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StudentFinancialSummaryResponse(

        UUID studentAccountId,

        UUID studentId,

        String registrationNumber,

        String studentName,

        UUID academicYearId,

        String academicYear,

        UUID levelId,

        String level,

        BigDecimal totalCharged,

        BigDecimal totalPaid,

        BigDecimal totalDiscount,

        BigDecimal balance,

        long overdueChargeCount,

        StudentAccountStatus status,

        List<StudentChargeResponse> charges
) {
}