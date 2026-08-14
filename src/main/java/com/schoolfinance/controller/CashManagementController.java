package com.schoolfinance.controller;

import com.schoolfinance.dto.cash.*;
import com.schoolfinance.service.CashManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cash")
@RequiredArgsConstructor
public class CashManagementController {

    private final CashManagementService service;


    @PostMapping("/sessions/open")
    @PreAuthorize("hasAuthority('ACCOUNTING_ENTRY_CREATE')")
    public CashSessionResponse open(
            @Valid
            @RequestBody
            OpenCashSessionRequest request
    ) {

        return service.open(
                request
        );
    }


    @GetMapping("/sessions/current")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public CashSessionDetailsResponse current(
            @RequestParam
            UUID establishmentId,

            @RequestParam(
                    required = false,
                    defaultValue = "571000"
            )
            String accountCode
    ) {

        return service.current(
                establishmentId,
                accountCode
        );
    }


    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public CashSessionDetailsResponse get(
            @PathVariable
            UUID sessionId
    ) {

        return service.get(
                sessionId
        );
    }


    @GetMapping("/sessions")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public List<CashSessionResponse> list(
            @RequestParam
            UUID establishmentId
    ) {

        return service.list(
                establishmentId
        );
    }


    @PostMapping("/sessions/{sessionId}/close")
    @PreAuthorize("hasAuthority('ACCOUNTING_ENTRY_CREATE')")
    public CashSessionDetailsResponse close(
            @PathVariable
            UUID sessionId,

            @Valid
            @RequestBody
            CloseCashSessionRequest request
    ) {

        return service.close(
                sessionId,
                request
        );
    }
}