package com.schoolfinance.controller;

import com.schoolfinance.dto.expense.ExpensePaymentResponse;
import com.schoolfinance.dto.expense.PayExpenseRequest;
import com.schoolfinance.service.ExpensePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpensePaymentController {

    private final ExpensePaymentService service;


    @PostMapping("/{expenseId}/pay")
    @PreAuthorize("hasAuthority('EXPENSE_PAY')")
    public ExpensePaymentResponse pay(
            @PathVariable
            UUID expenseId,

            @Valid
            @RequestBody
            PayExpenseRequest request
    ) {

        return service.pay(
                expenseId,
                request
        );
    }
}