package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.PaymentMethod;
import com.schoolfinance.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PaymentResponse(

        UUID id,

        String paymentNumber,

        UUID studentId,

        String registrationNumber,

        String studentName,

        UUID academicYearId,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        PaymentStatus status,

        String transactionReference,

        String notes,

        LocalDateTime paidAt,

        String receivedBy,

        BigDecimal accountBalance,

        List<PaymentAllocationResponse> allocations,

        ReceiptResponse receipt
) {
}