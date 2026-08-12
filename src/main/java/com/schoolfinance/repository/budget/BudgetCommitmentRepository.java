package com.schoolfinance.repository.budget;

import com.schoolfinance.entity.budget.BudgetCommitment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetCommitmentRepository
        extends JpaRepository<BudgetCommitment, UUID> {

    boolean existsByExpenseRequestId(
            UUID expenseRequestId
    );

    Optional<BudgetCommitment>
    findByExpenseRequestId(
            UUID expenseRequestId
    );

    List<BudgetCommitment>
    findByBudgetLineIdOrderByCommittedAtDesc(
            UUID budgetLineId
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select bc
            from BudgetCommitment bc
            join fetch bc.budgetLine bl
            join fetch bl.budget b
            where bc.expenseRequest.id = :expenseId
            """)
    Optional<BudgetCommitment>
    findByExpenseRequestIdForUpdate(
            @Param("expenseId")
            UUID expenseId
    );
}