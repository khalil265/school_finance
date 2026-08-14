package com.schoolfinance.dto.cash;

import java.util.List;

public record CashSessionDetailsResponse(

        CashSessionResponse session,

        List<CashMovementResponse> movements
) {
}