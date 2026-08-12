package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentAllocationRepository
        extends JpaRepository<PaymentAllocation, UUID> {

    List<PaymentAllocation>
    findByPaymentIdOrderByCreatedAtAsc(
            UUID paymentId
    );
}