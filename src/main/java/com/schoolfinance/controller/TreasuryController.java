package com.schoolfinance.controller;

import com.schoolfinance.dto.treasury.TreasuryTransactionResponse;
import com.schoolfinance.service.TreasuryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/treasury")
@RequiredArgsConstructor
public class TreasuryController {

    private final TreasuryService treasuryService;


    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public List<TreasuryTransactionResponse> transactions(
            @RequestParam
            UUID establishmentId
    ) {

        return treasuryService.list(
                establishmentId
        );
    }


    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('ACCOUNTING_READ')")
    public BigDecimal balance(
            @RequestParam
            UUID establishmentId
    ) {

        return treasuryService.balance(
                establishmentId
        );
    }
}