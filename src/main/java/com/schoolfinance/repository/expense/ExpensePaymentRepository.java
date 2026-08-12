package com.schoolfinance.repository.expense;

import com.schoolfinance.entity.expense.ExpensePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExpensePaymentRepository
        extends JpaRepository<ExpensePayment, UUID> {

    boolean existsByExpenseRequestId(
            UUID expenseRequestId
    );

    Optional<ExpensePayment> findByExpenseRequestId(
            UUID expenseRequestId
    );
}