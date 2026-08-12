package com.schoolfinance.dto.budget;

import com.schoolfinance.enums.BudgetCommitmentStatus;
import com.schoolfinance.enums.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BudgetCommitmentResponse(

        UUID commitmentId,

        UUID expenseId,

        String expenseNumber,

        UUID budgetLineId,

        String budgetLineCode,

        String budgetLineName,

        BigDecimal expenseAmount,

        BigDecimal committedAmount,

        BigDecimal budgetLineAvailable,

        BudgetCommitmentStatus commitmentStatus,

        ExpenseStatus expenseStatus,

        String committedBy,

        LocalDateTime committedAt
) {
}