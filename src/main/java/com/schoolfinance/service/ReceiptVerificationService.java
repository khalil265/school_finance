package com.schoolfinance.service;

import com.schoolfinance.dto.finance.ReceiptVerificationResponse;
import com.schoolfinance.entity.finance.Payment;
import com.schoolfinance.entity.finance.Receipt;
import com.schoolfinance.repository.finance.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptVerificationService {

    private final ReceiptRepository receiptRepository;


    @Transactional(readOnly = true)
    public ReceiptVerificationResponse verify(
            UUID verificationCode
    ) {

        Receipt receipt =
                receiptRepository
                        .findByVerificationCode(
                                verificationCode
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Recu introuvable."
                                )
                        );


        Payment payment =
                receipt.getPayment();


        boolean valid =
                !Boolean.TRUE.equals(
                        receipt.getCancelled()
                )
                && payment.getStatus()
                        != null;


        return new ReceiptVerificationResponse(
                valid,
                receipt.getVerificationCode(),
                receipt.getReceiptNumber(),
                payment.getPaymentNumber(),
                payment
                        .getStudentAccount()
                        .getEstablishment()
                        .getName(),
                receipt.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                receipt.getIssuedAt(),
                receipt.getCancelled()
        );
    }
}