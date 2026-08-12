package com.schoolfinance.controller;

import com.schoolfinance.dto.budget.BudgetCheckRequest;
import com.schoolfinance.dto.budget.BudgetCommitmentResponse;
import com.schoolfinance.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseBudgetController {

    private final BudgetService budgetService;


    @PostMapping("/{expenseId}/budget-check")
    @PreAuthorize("hasAuthority('EXPENSE_VERIFY')")
    public BudgetCommitmentResponse budgetCheck(
            @PathVariable
            UUID expenseId,

            @Valid
            @RequestBody
            BudgetCheckRequest request
    ) {

        return budgetService.checkAndCommit(
                expenseId,
                request.budgetLineId()
        );
    }
}