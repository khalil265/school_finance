package com.schoolfinance.repository.accounting;

import com.schoolfinance.entity.accounting.AccountingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountingEntryRepository
        extends JpaRepository<AccountingEntry, UUID> {

    List<AccountingEntry>
    findByEstablishmentIdOrderByEntryDateDesc(
            UUID establishmentId
    );
}