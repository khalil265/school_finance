package com.schoolfinance.controller;

import com.schoolfinance.dto.expense.SupplierRequest;
import com.schoolfinance.dto.expense.SupplierResponse;
import com.schoolfinance.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;


    @PostMapping
    @PreAuthorize("hasAuthority('EXPENSE_CREATE')")
    public SupplierResponse create(
            @Valid
            @RequestBody
            SupplierRequest request
    ) {

        return supplierService.create(
                request
        );
    }


    @GetMapping
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public List<SupplierResponse> list(
            @RequestParam
            UUID establishmentId
    ) {

        return supplierService.list(
                establishmentId
        );
    }
}