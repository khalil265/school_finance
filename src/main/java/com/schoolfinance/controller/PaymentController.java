package com.schoolfinance.controller;

import com.schoolfinance.dto.finance.PaymentRequest;
import com.schoolfinance.dto.finance.PaymentResponse;
import com.schoolfinance.dto.finance.ReceiptResponse;
import com.schoolfinance.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;


    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid
            @RequestBody
            PaymentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createPayment(
                                request
                        )
                );
    }


    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public PaymentResponse getPayment(
            @PathVariable
            UUID id
    ) {

        return service.getPayment(
                id
        );
    }


    @GetMapping("/payments/student/{studentId}")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public List<PaymentResponse> getStudentPayments(
            @PathVariable
            UUID studentId,

            @RequestParam
            UUID academicYearId
    ) {

        return service.getStudentPayments(
                studentId,
                academicYearId
        );
    }


    @GetMapping("/receipts/{id}")
    @PreAuthorize("hasAuthority('RECEIPT_PRINT')")
    public ReceiptResponse getReceipt(
            @PathVariable
            UUID id
    ) {

        return service.getReceipt(
                id
        );
    }
}