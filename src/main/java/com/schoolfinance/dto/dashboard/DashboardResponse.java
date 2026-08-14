package com.schoolfinance.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(

        UUID establishmentId,

        BigDecimal totalIncome,

        BigDecimal totalExpenses,

        BigDecimal budgetAmount,

        BigDecimal budgetCommitted,

        BigDecimal budgetConsumed,

        BigDecimal budgetAvailable,

        BigDecimal cashBalance,

        long pendingExpenses,

        long approvedExpenses,

        long paidExpenses,

        List<DashboardRecentTransactionResponse> recentTransactions
) {
}