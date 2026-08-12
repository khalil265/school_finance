package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeTypeRepository
        extends JpaRepository<FeeType, UUID> {

    boolean existsByEstablishmentIdAndCodeIgnoreCase(
            UUID establishmentId,
            String code
    );

    Optional<FeeType> findByEstablishmentIdAndCodeIgnoreCase(
            UUID establishmentId,
            String code
    );

    List<FeeType> findByEstablishmentIdAndActiveTrueOrderByNameAsc(
            UUID establishmentId
    );
}