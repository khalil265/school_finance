package com.schoolfinance.dto.cash;

import com.schoolfinance.enums.CashSessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CashSessionResponse(

        UUID id,

        UUID establishmentId,

        String sessionNumber,

        String accountCode,

        CashSessionStatus status,

        BigDecimal openingBalance,

        BigDecimal totalInflows,

        BigDecimal totalOutflows,

        BigDecimal theoreticalBalance,

        BigDecimal physicalBalance,

        BigDecimal differenceAmount,

        LocalDateTime openedAt,

        String openedBy,

        LocalDateTime closedAt,

        String closedBy,

        String closingNotes
) {
}