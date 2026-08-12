package com.schoolfinance.controller;

import com.schoolfinance.dto.budget.*;
import com.schoolfinance.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;


    @PostMapping
    @PreAuthorize("hasAuthority('BUDGET_CREATE')")
    public BudgetResponse create(
            @Valid
            @RequestBody
            CreateBudgetRequest request
    ) {

        return budgetService.createBudget(
                request
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BUDGET_READ')")
    public BudgetResponse get(
            @PathVariable
            UUID id
    ) {

        return budgetService.get(id);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('BUDGET_READ')")
    public List<BudgetResponse> list(
            @RequestParam
            UUID establishmentId
    ) {

        return budgetService.list(
                establishmentId
        );
    }


    @PostMapping("/{budgetId}/lines")
    @PreAuthorize("hasAuthority('BUDGET_CREATE')")
    public BudgetLineResponse addLine(
            @PathVariable
            UUID budgetId,

            @Valid
            @RequestBody
            CreateBudgetLineRequest request
    ) {

        return budgetService.addLine(
                budgetId,
                request
        );
    }


    @GetMapping("/{budgetId}/lines")
    @PreAuthorize("hasAuthority('BUDGET_READ')")
    public List<BudgetLineResponse> lines(
            @PathVariable
            UUID budgetId
    ) {

        return budgetService.lines(
                budgetId
        );
    }


    @PostMapping("/{budgetId}/activate")
    @PreAuthorize("hasAuthority('BUDGET_APPROVE')")
    public BudgetResponse activate(
            @PathVariable
            UUID budgetId
    ) {

        return budgetService.activate(
                budgetId
        );
    }
}