package com.schoolfinance.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceiptResponse(

        UUID id,

        String receiptNumber,

        UUID verificationCode,

        BigDecimal amount,

        LocalDateTime issuedAt,

        Boolean cancelled
) {
}