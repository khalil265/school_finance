package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.FeeCategory;
import com.schoolfinance.enums.FeeFrequency;

import java.util.UUID;

public record FeeTypeResponse(

        UUID id,

        UUID establishmentId,

        String establishmentName,

        String code,

        String name,

        FeeCategory category,

        FeeFrequency frequency,

        String description,

        Boolean mandatory,

        Boolean active
) {
}