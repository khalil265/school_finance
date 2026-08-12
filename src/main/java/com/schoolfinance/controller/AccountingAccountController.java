package com.schoolfinance.controller;

import com.schoolfinance.dto.accounting.AccountingAccountResponse;
import com.schoolfinance.dto.accounting.CreateAccountingAccountRequest;
import com.schoolfinance.dto.accounting.UpdateAccountingAccountRequest;
import com.schoolfinance.enums.AccountingAccountType;
import com.schoolfinance.service.AccountingAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounting/accounts")
@RequiredArgsConstructor
public class AccountingAccountController {

    private final AccountingAccountService service;


    @PostMapping
    @PreAuthorize("hasAuthority('ACCOUNTING_ENTRY_CREATE')")
    public AccountingAccountResponse create(
            @Valid
            @RequestBody
            CreateAccountingAccountRequest request
    ) {

        return service.create(request);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public List<AccountingAccountResponse> list(
            @RequestParam
            UUID establishmentId,

            @RequestParam(required = false)
            AccountingAccountType type,

            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            Boolean activeOnly
    ) {

        return service.list(
                establishmentId,
                type,
                activeOnly
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public AccountingAccountResponse get(
            @PathVariable
            UUID id
    ) {

        return service.get(id);
    }


    @GetMapping("/by-code/{code}")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public AccountingAccountResponse getByCode(
            @PathVariable
            String code,

            @RequestParam
            UUID establishmentId
    ) {

        return service.getByCode(
                establishmentId,
                code
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCOUNTING_ENTRY_CREATE')")
    public AccountingAccountResponse update(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateAccountingAccountRequest request
    ) {

        return service.update(
                id,
                request
        );
    }


    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ACCOUNTING_ENTRY_CREATE')")
    public AccountingAccountResponse deactivate(
            @PathVariable
            UUID id
    ) {

        return service.deactivate(id);
    }
}