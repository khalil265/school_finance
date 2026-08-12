package com.schoolfinance.dto.accounting;

import com.schoolfinance.enums.AccountingAccountType;

import java.util.UUID;

public record AccountingAccountResponse(

        UUID id,

        UUID establishmentId,

        String code,

        String name,

        AccountingAccountType accountType,

        UUID parentId,

        String parentCode,

        String parentName,

        String description,

        Boolean postingAllowed,

        Boolean systemAccount,

        Boolean active
) {
}