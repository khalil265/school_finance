package com.schoolfinance.dto.finance;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GenerateScheduleRequest(

        @NotNull
        UUID enrollmentId
) {
}