package com.schoolfinance.controller;

import com.schoolfinance.dto.expense.CreateExpenseRequest;
import com.schoolfinance.dto.expense.ExpenseResponse;
import com.schoolfinance.dto.expense.RejectExpenseRequest;
import com.schoolfinance.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;


    @PostMapping
    @PreAuthorize("hasAuthority('EXPENSE_CREATE')")
    public ExpenseResponse create(
            @Valid
            @RequestBody
            CreateExpenseRequest request
    ) {

        return expenseService.create(
                request
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public ExpenseResponse get(
            @PathVariable
            UUID id
    ) {

        return expenseService.get(id);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public List<ExpenseResponse> list(
            @RequestParam
            UUID establishmentId
    ) {

        return expenseService.list(
                establishmentId
        );
    }


    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('EXPENSE_CREATE')")
    public ExpenseResponse submit(
            @PathVariable
            UUID id
    ) {

        return expenseService.submit(id);
    }


    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('EXPENSE_VERIFY')")
    public ExpenseResponse verify(
            @PathVariable
            UUID id
    ) {

        return expenseService.verify(id);
    }


    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('EXPENSE_APPROVE')")
    public ExpenseResponse approve(
            @PathVariable
            UUID id
    ) {

        return expenseService.approve(id);
    }


    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('EXPENSE_VERIFY','EXPENSE_APPROVE')")
    public ExpenseResponse reject(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            RejectExpenseRequest request
    ) {

        return expenseService.reject(
                id,
                request.reason()
        );
    }
}