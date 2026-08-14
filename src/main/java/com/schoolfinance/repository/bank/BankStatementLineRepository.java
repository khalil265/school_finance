package com.schoolfinance.repository.bank;

import com.schoolfinance.entity.bank.BankStatementLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankStatementLineRepository
        extends JpaRepository<BankStatementLine, UUID> {

    List<BankStatementLine>
    findByBankStatementIdOrderByTransactionDateAscCreatedAtAsc(
            UUID bankStatementId
    );

    boolean existsByAccountingEntryLineId(
            UUID accountingEntryLineId
    );
}