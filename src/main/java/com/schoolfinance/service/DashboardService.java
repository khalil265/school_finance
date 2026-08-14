package com.schoolfinance.service;

import com.schoolfinance.dto.dashboard.DashboardRecentTransactionResponse;
import com.schoolfinance.dto.dashboard.DashboardResponse;
import com.schoolfinance.entity.budget.Budget;
import com.schoolfinance.entity.expense.ExpenseRequest;
import com.schoolfinance.entity.finance.Payment;
import com.schoolfinance.entity.treasury.TreasuryTransaction;
import com.schoolfinance.enums.ExpenseStatus;
import com.schoolfinance.repository.budget.BudgetRepository;
import com.schoolfinance.repository.cash.CashSessionRepository;
import com.schoolfinance.repository.expense.ExpenseRequestRepository;
import com.schoolfinance.repository.finance.PaymentRepository;
import com.schoolfinance.repository.treasury.TreasuryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PaymentRepository paymentRepository;

    private final ExpenseRequestRepository expenseRepository;

    private final BudgetRepository budgetRepository;

    private final TreasuryTransactionRepository treasuryRepository;

    private final CashSessionRepository cashSessionRepository;


    @Transactional(readOnly = true)
    public DashboardResponse summary(
            UUID establishmentId
    ) {

        List<Payment> payments =
                paymentRepository
                        .findByStudentAccountAcademicYearEstablishmentIdAndActiveTrueOrderByPaidAtDesc(
                                establishmentId
                        );


        BigDecimal totalIncome =
                payments.stream()
                        .map(Payment::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        List<ExpenseRequest> expenses =
                expenseRepository
                        .findByEstablishmentIdOrderByCreatedAtDesc(
                                establishmentId
                        );


        BigDecimal totalExpenses =
                expenses.stream()
                        .filter(expense ->
                                expense.getStatus()
                                        == ExpenseStatus.PAID
                        )
                        .map(ExpenseRequest::getAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        long pendingExpenses =
                expenses.stream()
                        .filter(expense ->
                                expense.getStatus()
                                        != ExpenseStatus.PAID
                                &&
                                expense.getStatus()
                                        != ExpenseStatus.REJECTED
                        )
                        .count();


        long approvedExpenses =
                expenses.stream()
                        .filter(expense ->
                                expense.getStatus()
                                        == ExpenseStatus.APPROVED
                        )
                        .count();


        long paidExpenses =
                expenses.stream()
                        .filter(expense ->
                                expense.getStatus()
                                        == ExpenseStatus.PAID
                        )
                        .count();


        List<Budget> budgets =
                budgetRepository
                        .findByEstablishmentIdOrderByCreatedAtDesc(
                                establishmentId
                        );


        BigDecimal budgetAmount =
                budgets.stream()
                        .map(Budget::getTotalAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal budgetCommitted =
                budgets.stream()
                        .map(Budget::getTotalCommitted)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal budgetConsumed =
                budgets.stream()
                        .map(Budget::getTotalConsumed)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal budgetAvailable =
                budgetAmount
                        .subtract(
                                budgetCommitted
                        )
                        .subtract(
                                budgetConsumed
                        );


        BigDecimal cashBalance =
                cashSessionRepository
                        .findFirstByEstablishmentIdAndAccountCodeIgnoreCaseAndStatusOrderByOpenedAtDesc(
                                establishmentId,
                                "571000",
                                com.schoolfinance.enums.CashSessionStatus.OPEN
                        )
                        .map(session ->
                                session.getTheoreticalBalance() == null
                                        ? session.getOpeningBalance()
                                        : session.getTheoreticalBalance()
                        )
                        .orElse(
                                BigDecimal.ZERO
                        );


        List<TreasuryTransaction> treasuryTransactions =
                treasuryRepository
                        .findByEstablishmentIdOrderByTransactionDateDesc(
                                establishmentId
                        );


        List<DashboardRecentTransactionResponse> recent =
                new ArrayList<>();


        for (
                TreasuryTransaction transaction
                : treasuryTransactions
        ) {

            recent.add(
                    new DashboardRecentTransactionResponse(
                            transaction.getId(),
                            transaction.getTransactionNumber(),
                            transaction.getTransactionType()
                                    .name(),
                            transaction.getDescription(),
                            transaction.getAmount(),
                            transaction.getTransactionDate()
                    )
            );
        }


        for (
                Payment payment
                : payments
        ) {

            recent.add(
                    new DashboardRecentTransactionResponse(
                            payment.getId(),
                            payment.getPaymentNumber(),
                            "INCOME",
                            "Paiement eleve "
                                    + payment.getStudentAccount()
                                            .getStudent()
                                            .getRegistrationNumber(),
                            payment.getAmount(),
                            payment.getPaidAt()
                    )
            );
        }


        recent =
                recent.stream()
                        .sorted(
                                Comparator.comparing(
                                        DashboardRecentTransactionResponse::transactionDate,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                        )
                        .limit(10)
                        .toList();


        return new DashboardResponse(
                establishmentId,
                totalIncome,
                totalExpenses,
                budgetAmount,
                budgetCommitted,
                budgetConsumed,
                budgetAvailable,
                cashBalance,
                pendingExpenses,
                approvedExpenses,
                paidExpenses,
                recent
        );
    }
}