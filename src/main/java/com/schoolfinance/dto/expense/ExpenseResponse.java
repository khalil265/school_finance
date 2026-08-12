package com.schoolfinance.dto.expense;

import com.schoolfinance.enums.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponse(

        UUID id,

        String expenseNumber,

        UUID establishmentId,

        UUID supplierId,

        String supplierName,

        String subject,

        String description,

        BigDecimal amount,

        String currency,

        ExpenseStatus status,

        String requestedBy,

        LocalDateTime submittedAt,

        String verifiedBy,

        LocalDateTime verifiedAt,

        String approvedBy,

        LocalDateTime approvedAt,

        String rejectedBy,

        LocalDateTime rejectedAt,

        String rejectionReason,

        LocalDateTime paidAt,

        String paymentReference
) {
}