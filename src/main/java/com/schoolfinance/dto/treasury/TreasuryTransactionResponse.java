package com.schoolfinance.dto.treasury;

import com.schoolfinance.enums.PaymentMethod;
import com.schoolfinance.enums.TreasuryTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TreasuryTransactionResponse(

        UUID id,

        String transactionNumber,

        TreasuryTransactionType transactionType,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String accountCode,

        String externalReference,

        String description,

        LocalDateTime transactionDate,

        String createdBy
) {
}