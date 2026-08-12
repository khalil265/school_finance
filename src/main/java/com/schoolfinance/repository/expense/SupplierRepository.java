package com.schoolfinance.repository.expense;

import com.schoolfinance.entity.expense.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository
        extends JpaRepository<Supplier, UUID> {

    List<Supplier> findByEstablishmentIdOrderByNameAsc(
            UUID establishmentId
    );

    Optional<Supplier> findByEstablishmentIdAndCode(
            UUID establishmentId,
            String code
    );

    boolean existsByEstablishmentIdAndCode(
            UUID establishmentId,
            String code
    );
}