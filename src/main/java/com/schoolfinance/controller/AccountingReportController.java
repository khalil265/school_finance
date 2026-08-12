package com.schoolfinance.controller;

import com.schoolfinance.dto.accounting.*;
import com.schoolfinance.service.AccountingReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
public class AccountingReportController {

    private final AccountingReportService service;


    @GetMapping("/journal")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public List<GeneralJournalLineResponse> journal(
            @RequestParam
            UUID establishmentId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return service.generalJournal(
                establishmentId,
                from,
                to
        );
    }


    @GetMapping("/ledger")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public LedgerResponse ledger(
            @RequestParam
            UUID establishmentId,

            @RequestParam
            String accountCode,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return service.ledger(
                establishmentId,
                accountCode,
                from,
                to
        );
    }


    @GetMapping("/trial-balance")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public TrialBalanceResponse trialBalance(
            @RequestParam
            UUID establishmentId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return service.trialBalance(
                establishmentId,
                from,
                to
        );
    }


    @GetMapping("/accounts/{accountCode}/balance")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public AccountBalanceResponse accountBalance(
            @PathVariable
            String accountCode,

            @RequestParam
            UUID establishmentId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return service.accountBalance(
                establishmentId,
                accountCode,
                from,
                to
        );
    }
}