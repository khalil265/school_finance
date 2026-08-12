package com.schoolfinance.dto.finance;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ScheduleGenerationResponse(

        UUID studentAccountId,

        UUID studentId,

        String registrationNumber,

        int createdCharges,

        int skippedCharges,

        BigDecimal totalCharged,

        BigDecimal balance,

        List<StudentChargeResponse> charges
) {
}