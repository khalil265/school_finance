package com.schoolfinance.repository.budget;

import com.schoolfinance.entity.budget.Budget;
import com.schoolfinance.enums.BudgetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository
        extends JpaRepository<Budget, UUID> {

    boolean existsByEstablishmentIdAndAcademicYearId(
            UUID establishmentId,
            UUID academicYearId
    );

    Optional<Budget>
    findByEstablishmentIdAndAcademicYearId(
            UUID establishmentId,
            UUID academicYearId
    );

    List<Budget>
    findByEstablishmentIdOrderByCreatedAtDesc(
            UUID establishmentId
    );

    List<Budget>
    findByEstablishmentIdAndStatus(
            UUID establishmentId,
            BudgetStatus status
    );
}