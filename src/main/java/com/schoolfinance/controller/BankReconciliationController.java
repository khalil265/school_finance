package com.schoolfinance.controller;

import com.schoolfinance.dto.bank.*;
import com.schoolfinance.service.BankReconciliationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bank-reconciliation")
@RequiredArgsConstructor
public class BankReconciliationController {

    private final BankReconciliationService service;


    @PostMapping("/statements")
    @PreAuthorize("hasAuthority('BANK_RECONCILE')")
    public BankStatementResponse createStatement(
            @Valid
            @RequestBody
            CreateBankStatementRequest request
    ) {

        return service.createStatement(
                request
        );
    }


    @GetMapping("/statements")
    @PreAuthorize("hasAuthority('BANK_READ')")
    public List<BankStatementResponse> listStatements(
            @RequestParam
            UUID establishmentId
    ) {

        return service.listStatements(
                establishmentId
        );
    }


    @PostMapping("/statements/{statementId}/lines")
    @PreAuthorize("hasAuthority('BANK_RECONCILE')")
    public BankStatementLineResponse addLine(
            @PathVariable
            UUID statementId,

            @Valid
            @RequestBody
            CreateBankStatementLineRequest request
    ) {

        return service.addLine(
                statementId,
                request
        );
    }


    @GetMapping("/statements/{statementId}/lines")
    @PreAuthorize("hasAuthority('BANK_READ')")
    public List<BankStatementLineResponse> lines(
            @PathVariable
            UUID statementId
    ) {

        return service.getLines(
                statementId
        );
    }


    @GetMapping("/lines/{lineId}/candidates")
    @PreAuthorize("hasAuthority('BANK_READ')")
    public List<BankReconciliationCandidateResponse> candidates(
            @PathVariable
            UUID lineId
    ) {

        return service.candidates(
                lineId
        );
    }


    @PostMapping("/lines/{lineId}/reconcile")
    @PreAuthorize("hasAuthority('BANK_RECONCILE')")
    public BankStatementLineResponse reconcile(
            @PathVariable
            UUID lineId,

            @Valid
            @RequestBody
            ReconcileBankLineRequest request
    ) {

        return service.reconcile(
                lineId,
                request
        );
    }


    @PostMapping("/statements/{statementId}/close")
    @PreAuthorize("hasAuthority('BANK_RECONCILE')")
    public BankStatementResponse close(
            @PathVariable
            UUID statementId
    ) {

        return service.closeStatement(
                statementId
        );
    }
}