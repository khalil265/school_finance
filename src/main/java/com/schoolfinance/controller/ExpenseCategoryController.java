package com.schoolfinance.controller;

import com.schoolfinance.dto.expense.ExpenseCategoryRequest;
import com.schoolfinance.dto.expense.ExpenseCategoryResponse;
import com.schoolfinance.service.ExpenseCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService service;


    @PostMapping
    @PreAuthorize("hasAuthority('EXPENSE_CREATE')")
    public ResponseEntity<ExpenseCategoryResponse> create(
            @Valid
            @RequestBody
            ExpenseCategoryRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.create(request)
                );
    }


    @GetMapping
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public List<ExpenseCategoryResponse> list(
            @RequestParam
            UUID establishmentId
    ) {

        return service.list(
                establishmentId
        );
    }
}