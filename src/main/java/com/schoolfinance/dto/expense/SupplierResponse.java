package com.schoolfinance.dto.expense;

import java.util.UUID;

public record SupplierResponse(

        UUID id,

        UUID establishmentId,

        String code,

        String name,

        String taxIdentifier,

        String phone,

        String email,

        String address,

        String bankName,

        String bankAccount,

        Boolean active
) {
}