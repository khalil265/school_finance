package com.schoolfinance.repository.expense;

import com.schoolfinance.entity.expense.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseCategoryRepository
        extends JpaRepository<ExpenseCategory, UUID> {

    List<ExpenseCategory>
    findByEstablishmentIdAndActiveTrueOrderByNameAsc(
            UUID establishmentId
    );

    boolean existsByEstablishmentIdAndCodeIgnoreCase(
            UUID establishmentId,
            String code
    );
}