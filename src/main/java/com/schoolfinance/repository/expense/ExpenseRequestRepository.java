package com.schoolfinance.repository.expense;

import com.schoolfinance.entity.expense.ExpenseRequest;
import com.schoolfinance.enums.ExpenseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRequestRepository
        extends JpaRepository<ExpenseRequest, UUID> {

    Optional<ExpenseRequest> findByExpenseNumber(
            String expenseNumber
    );

    List<ExpenseRequest>
    findByEstablishmentIdOrderByCreatedAtDesc(
            UUID establishmentId
    );

    List<ExpenseRequest>
    findByEstablishmentIdAndStatusOrderByCreatedAtDesc(
            UUID establishmentId,
            ExpenseStatus status
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select e
            from ExpenseRequest e
            left join fetch e.supplier
            join fetch e.establishment
            where e.id = :id
            """)
    Optional<ExpenseRequest> findByIdForUpdate(
            @Param("id")
            UUID id
    );
}