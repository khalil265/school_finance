package com.schoolfinance.repository.budget;

import com.schoolfinance.entity.budget.BudgetLine;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetLineRepository
        extends JpaRepository<BudgetLine, UUID> {

    boolean existsByBudgetIdAndCodeIgnoreCase(
            UUID budgetId,
            String code
    );

    List<BudgetLine>
    findByBudgetIdAndActiveTrueOrderByCodeAsc(
            UUID budgetId
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select bl
            from BudgetLine bl
            join fetch bl.budget b
            where bl.id = :id
            """)
    Optional<BudgetLine> findByIdForUpdate(
            @Param("id")
            UUID id
    );
}