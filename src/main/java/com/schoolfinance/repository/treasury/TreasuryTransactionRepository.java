package com.schoolfinance.repository.treasury;

import com.schoolfinance.entity.treasury.TreasuryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TreasuryTransactionRepository
        extends JpaRepository<TreasuryTransaction, UUID> {

    List<TreasuryTransaction>
    findByEstablishmentIdOrderByTransactionDateDesc(
            UUID establishmentId
    );
}