package com.schoolfinance.repository.accounting;

import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.enums.AccountingAccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountingAccountRepository
        extends JpaRepository<AccountingAccount, UUID> {

    boolean existsByEstablishmentIdAndCodeIgnoreCase(
            UUID establishmentId,
            String code
    );

    Optional<AccountingAccount>
    findByEstablishmentIdAndCodeIgnoreCase(
            UUID establishmentId,
            String code
    );

    List<AccountingAccount>
    findByEstablishmentIdOrderByCodeAsc(
            UUID establishmentId
    );

    List<AccountingAccount>
    findByEstablishmentIdAndActiveTrueOrderByCodeAsc(
            UUID establishmentId
    );

    List<AccountingAccount>
    findByEstablishmentIdAndAccountTypeAndActiveTrueOrderByCodeAsc(
            UUID establishmentId,
            AccountingAccountType accountType
    );

    List<AccountingAccount>
    findByParentIdOrderByCodeAsc(
            UUID parentId
    );
}