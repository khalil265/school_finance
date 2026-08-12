package com.schoolfinance.dto.expense;

import com.schoolfinance.dto.accounting.AccountingEntryResponse;
import com.schoolfinance.enums.BudgetCommitmentStatus;
import com.schoolfinance.enums.ExpenseStatus;
import com.schoolfinance.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpensePaymentResponse(

        UUID paymentId,

        String paymentNumber,

        UUID expenseId,

        String expenseNumber,

        String supplierName,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String paymentReference,

        LocalDateTime paidAt,

        String paidBy,

        ExpenseStatus expenseStatus,

        BudgetCommitmentStatus commitmentStatus,

        BigDecimal budgetCommittedAmount,

        BigDecimal budgetConsumedAmount,

        BigDecimal budgetAvailableAmount,

        UUID treasuryTransactionId,

        String treasuryTransactionNumber,

        AccountingEntryResponse accountingEntry
) {
}