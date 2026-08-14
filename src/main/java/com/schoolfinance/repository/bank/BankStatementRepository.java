package com.schoolfinance.repository.bank;

import com.schoolfinance.entity.bank.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankStatementRepository
        extends JpaRepository<BankStatement, UUID> {

    boolean existsByEstablishmentIdAndStatementReferenceIgnoreCase(
            UUID establishmentId,
            String statementReference
    );

    List<BankStatement>
    findByEstablishmentIdOrderByStartDateDesc(
            UUID establishmentId
    );
}