package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.PaymentMethod;
import com.schoolfinance.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceiptVerificationResponse(

        boolean valid,

        UUID verificationCode,

        String receiptNumber,

        String paymentNumber,

        String establishment,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        PaymentStatus paymentStatus,

        LocalDateTime issuedAt,

        Boolean cancelled
) {
}