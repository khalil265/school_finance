package com.schoolfinance.controller;

import com.schoolfinance.dto.finance.ReceiptVerificationResponse;
import com.schoolfinance.service.AuditService;
import com.schoolfinance.service.ReceiptVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/receipts")
@RequiredArgsConstructor
public class PublicReceiptController {

    private final ReceiptVerificationService verificationService;

    private final AuditService auditService;


    @GetMapping(
            "/verify/{verificationCode}"
    )
    public ReceiptVerificationResponse verify(
            @PathVariable
            UUID verificationCode
    ) {

        ReceiptVerificationResponse response =
                verificationService
                        .verify(
                                verificationCode
                        );


        auditService.log(
                "RECEIPT_VERIFIED",
                "Receipt",
                response.receiptNumber(),
                null,
                verificationCode
        );


        return response;
    }
}