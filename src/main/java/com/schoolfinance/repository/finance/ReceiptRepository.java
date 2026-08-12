package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository
        extends JpaRepository<Receipt, UUID> {

    Optional<Receipt> findByPaymentId(
            UUID paymentId
    );

    Optional<Receipt> findByReceiptNumber(
            String receiptNumber
    );

    Optional<Receipt> findByVerificationCode(
            UUID verificationCode
    );
}