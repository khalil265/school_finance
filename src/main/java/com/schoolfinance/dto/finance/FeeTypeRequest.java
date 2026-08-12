package com.schoolfinance.dto.finance;

import com.schoolfinance.enums.FeeCategory;
import com.schoolfinance.enums.FeeFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FeeTypeRequest(

        @NotNull
        UUID establishmentId,

        @NotBlank
        @Size(max = 50)
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        FeeCategory category,

        @NotNull
        FeeFrequency frequency,

        String description,

        Boolean mandatory
) {
}