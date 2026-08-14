package com.schoolfinance.repository.cash;

import com.schoolfinance.entity.cash.CashSession;
import com.schoolfinance.enums.CashSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashSessionRepository
        extends JpaRepository<CashSession, UUID> {

    boolean existsByEstablishmentIdAndAccountCodeIgnoreCaseAndStatus(
            UUID establishmentId,
            String accountCode,
            CashSessionStatus status
    );

    Optional<CashSession>
    findFirstByEstablishmentIdAndAccountCodeIgnoreCaseAndStatusOrderByOpenedAtDesc(
            UUID establishmentId,
            String accountCode,
            CashSessionStatus status
    );

    List<CashSession>
    findByEstablishmentIdOrderByOpenedAtDesc(
            UUID establishmentId
    );
}