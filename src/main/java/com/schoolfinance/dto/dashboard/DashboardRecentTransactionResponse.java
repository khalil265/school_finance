package com.schoolfinance.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardRecentTransactionResponse(

        UUID id,

        String reference,

        String type,

        String description,

        BigDecimal amount,

        LocalDateTime transactionDate
) {
}