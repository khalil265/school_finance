package com.schoolfinance.repository.accounting;

import com.schoolfinance.entity.accounting.AccountingJournal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountingJournalRepository
        extends JpaRepository<AccountingJournal, UUID> {

    Optional<AccountingJournal>
    findByEstablishmentIdAndCode(
            UUID establishmentId,
            String code
    );
}