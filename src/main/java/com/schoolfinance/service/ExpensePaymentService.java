package com.schoolfinance.service;

import com.schoolfinance.dto.accounting.AccountingEntryResponse;
import com.schoolfinance.dto.accounting.AccountingLineResponse;
import com.schoolfinance.dto.expense.ExpensePaymentResponse;
import com.schoolfinance.dto.expense.PayExpenseRequest;
import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.entity.accounting.AccountingEntry;
import com.schoolfinance.entity.accounting.AccountingEntryLine;
import com.schoolfinance.entity.accounting.AccountingJournal;
import com.schoolfinance.entity.budget.Budget;
import com.schoolfinance.entity.budget.BudgetCommitment;
import com.schoolfinance.entity.budget.BudgetLine;
import com.schoolfinance.entity.expense.ExpensePayment;
import com.schoolfinance.entity.expense.ExpenseRequest;
import com.schoolfinance.entity.treasury.TreasuryTransaction;
import com.schoolfinance.enums.*;
import com.schoolfinance.repository.accounting.AccountingEntryLineRepository;
import com.schoolfinance.repository.accounting.AccountingEntryRepository;
import com.schoolfinance.repository.accounting.AccountingJournalRepository;
import com.schoolfinance.repository.budget.BudgetCommitmentRepository;
import com.schoolfinance.repository.budget.BudgetLineRepository;
import com.schoolfinance.repository.budget.BudgetRepository;
import com.schoolfinance.repository.expense.ExpensePaymentRepository;
import com.schoolfinance.repository.expense.ExpenseRequestRepository;
import com.schoolfinance.repository.treasury.TreasuryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpensePaymentService {

    private final ExpenseRequestRepository expenseRepository;

    private final ExpensePaymentRepository expensePaymentRepository;

    private final BudgetCommitmentRepository commitmentRepository;

    private final BudgetLineRepository budgetLineRepository;

    private final BudgetRepository budgetRepository;

    private final TreasuryTransactionRepository treasuryRepository;

    private final AccountingJournalRepository journalRepository;

    private final AccountingEntryRepository accountingEntryRepository;

    private final AccountingEntryLineRepository accountingLineRepository;

    private final AuditService auditService;

    private final AccountingAccountValidationService accountValidationService;


    @Transactional
    public ExpensePaymentResponse pay(
            UUID expenseId,
            PayExpenseRequest request
    ) {

        ExpenseRequest expense =
                expenseRepository
                        .findByIdForUpdate(expenseId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Depense introuvable."
                                )
                        );


        if (
                expense.getStatus()
                        != ExpenseStatus.APPROVED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Seule une depense APPROVED peut etre payee."
            );
        }


        if (
                expensePaymentRepository
                        .existsByExpenseRequestId(
                                expenseId
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette depense a deja ete payee."
            );
        }


        validateReference(request);


        AccountingAccount expenseAccount =
                accountValidationService
                        .requirePostingAccount(
                                expense.getEstablishment().getId(),
                                request.expenseAccountCode(),
                                AccountingAccountType.EXPENSE
                        );


        AccountingAccount treasuryAccount =
                accountValidationService
                        .requirePostingAccount(
                                expense.getEstablishment().getId(),
                                request.treasuryAccountCode(),
                                AccountingAccountType.ASSET
                        );


        BudgetCommitment commitment =
                commitmentRepository
                        .findByExpenseRequestIdForUpdate(
                                expenseId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Aucun engagement budgetaire n'existe pour cette depense."
                                )
                        );


        if (
                commitment.getStatus()
                        != BudgetCommitmentStatus.RESERVED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "L'engagement budgetaire doit etre RESERVED."
            );
        }


        BudgetLine budgetLine =
                commitment.getBudgetLine();

        Budget budget =
                budgetLine.getBudget();


        if (
                budgetLine.getCommittedAmount()
                        .compareTo(
                                expense.getAmount()
                        ) < 0
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Montant engage insuffisant sur la ligne budgetaire."
            );
        }


        LocalDateTime now =
                LocalDateTime.now();

        String username =
                currentUsername();


        ExpensePayment payment =
                ExpensePayment.builder()
                        .expenseRequest(expense)
                        .paymentNumber(
                                generateNumber(
                                        "EPAY"
                                )
                        )
                        .amount(
                                expense.getAmount()
                        )
                        .paymentMethod(
                                request.paymentMethod()
                        )
                        .paymentReference(
                                clean(
                                        request.paymentReference()
                                )
                        )
                        .paidAt(now)
                        .paidBy(username)
                        .notes(
                                request.notes()
                        )
                        .build();


        payment =
                expensePaymentRepository.save(
                        payment
                );


        budgetLine.setCommittedAmount(
                budgetLine
                        .getCommittedAmount()
                        .subtract(
                                expense.getAmount()
                        )
        );

        budgetLine.setConsumedAmount(
                budgetLine
                        .getConsumedAmount()
                        .add(
                                expense.getAmount()
                        )
        );


        budgetLineRepository.save(
                budgetLine
        );


        commitment.setStatus(
                BudgetCommitmentStatus.CONSUMED
        );

        commitment.setConsumedAt(
                now
        );


        commitmentRepository.save(
                commitment
        );


        refreshBudgetTotals(
                budget
        );


        expense.setStatus(
                ExpenseStatus.PAID
        );

        expense.setPaidAt(now);

        expense.setPaymentReference(
                payment.getPaymentNumber()
        );


        expenseRepository.save(
                expense
        );


        TreasuryTransaction treasury =
                TreasuryTransaction.builder()
                        .establishment(
                                expense.getEstablishment()
                        )
                        .expensePayment(
                                payment
                        )
                        .transactionNumber(
                                generateNumber(
                                        "TRX"
                                )
                        )
                        .transactionType(
                                TreasuryTransactionType.EXPENSE
                        )
                        .amount(
                                expense.getAmount()
                        )
                        .paymentMethod(
                                request.paymentMethod()
                        )
                        .accountCode(
                                treasuryAccount.getCode()
                        )
                        .externalReference(
                                clean(
                                        request.paymentReference()
                                )
                        )
                        .description(
                                expense.getSubject()
                        )
                        .transactionDate(now)
                        .createdBy(username)
                        .build();


        treasury =
                treasuryRepository.save(
                        treasury
                );


        AccountingJournal journal =
                getOrCreateExpenseJournal(
                        expense
                );


        AccountingEntry entry =
                AccountingEntry.builder()
                        .establishment(
                                expense.getEstablishment()
                        )
                        .academicYear(
                                budget.getAcademicYear()
                        )
                        .journal(journal)
                        .expensePayment(payment)
                        .entryNumber(
                                generateNumber(
                                        "ACC"
                                )
                        )
                        .entryDate(now)
                        .description(
                                "Paiement "
                                        + expense.getExpenseNumber()
                                        + " - "
                                        + expense.getSubject()
                        )
                        .totalDebit(
                                expense.getAmount()
                        )
                        .totalCredit(
                                expense.getAmount()
                        )
                        .status(
                                AccountingEntryStatus.POSTED
                        )
                        .postedBy(username)
                        .postedAt(now)
                        .build();


        entry =
                accountingEntryRepository.save(
                        entry
                );


        AccountingEntryLine debit =
                AccountingEntryLine.builder()
                        .accountingEntry(entry)
                        .lineNumber(1)
                        .accountCode(
                                expenseAccount.getCode()
                        )
                        .accountName(
                                expenseAccount.getName()
                        )
                        .direction(
                                AccountingDirection.DEBIT
                        )
                        .amount(
                                expense.getAmount()
                        )
                        .description(
                                expense.getSubject()
                        )
                        .build();


        AccountingEntryLine credit =
                AccountingEntryLine.builder()
                        .accountingEntry(entry)
                        .lineNumber(2)
                        .accountCode(
                                treasuryAccount.getCode()
                        )
                        .accountName(
                                treasuryAccount.getName()
                        )
                        .direction(
                                AccountingDirection.CREDIT
                        )
                        .amount(
                                expense.getAmount()
                        )
                        .description(
                                "Reglement fournisseur "
                                        + (
                                        expense.getSupplier() == null
                                                ? ""
                                                : expense.getSupplier().getName()
                                )
                        )
                        .build();


        accountingLineRepository.saveAll(
                List.of(
                        debit,
                        credit
                )
        );


        auditService.log(
                "EXPENSE_PAID",
                "ExpenseRequest",
                expense.getId(),
                "APPROVED",
                "PAID - "
                        + payment.getPaymentNumber()
        );


        auditService.log(
                "BUDGET_COMMITMENT_CONSUMED",
                "BudgetCommitment",
                commitment.getId(),
                "RESERVED",
                "CONSUMED"
        );


        auditService.log(
                "TREASURY_EXPENSE_CREATED",
                "TreasuryTransaction",
                treasury.getId(),
                null,
                treasury.getTransactionNumber()
        );


        auditService.log(
                "ACCOUNTING_ENTRY_POSTED",
                "AccountingEntry",
                entry.getId(),
                null,
                entry.getEntryNumber()
        );


        return toResponse(
                payment,
                commitment,
                budgetLine,
                treasury,
                entry
        );
    }


    private AccountingJournal getOrCreateExpenseJournal(
            ExpenseRequest expense
    ) {

        return journalRepository
                .findByEstablishmentIdAndCode(
                        expense
                                .getEstablishment()
                                .getId(),
                        "ACHATS"
                )
                .orElseGet(() -> {

                    AccountingJournal journal =
                            AccountingJournal.builder()
                                    .establishment(
                                            expense.getEstablishment()
                                    )
                                    .code("ACHATS")
                                    .name(
                                            "Journal des achats et depenses"
                                    )
                                    .active(true)
                                    .build();

                    return journalRepository.save(
                            journal
                    );
                });
    }


    private void refreshBudgetTotals(
            Budget budget
    ) {

        List<BudgetLine> lines =
                budgetLineRepository
                        .findByBudgetIdAndActiveTrueOrderByCodeAsc(
                                budget.getId()
                        );


        BigDecimal total =
                lines.stream()
                        .map(
                                BudgetLine::getAllocatedAmount
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal committed =
                lines.stream()
                        .map(
                                BudgetLine::getCommittedAmount
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal consumed =
                lines.stream()
                        .map(
                                BudgetLine::getConsumedAmount
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        budget.setTotalAmount(total);

        budget.setTotalCommitted(
                committed
        );

        budget.setTotalConsumed(
                consumed
        );


        budgetRepository.save(
                budget
        );
    }


    private ExpensePaymentResponse toResponse(
            ExpensePayment payment,
            BudgetCommitment commitment,
            BudgetLine budgetLine,
            TreasuryTransaction treasury,
            AccountingEntry entry
    ) {

        List<AccountingEntryLine> lines =
                accountingLineRepository
                        .findByAccountingEntryIdOrderByLineNumberAsc(
                                entry.getId()
                        );


        List<AccountingLineResponse>
                lineResponses =
                lines.stream()
                        .map(line ->
                                new AccountingLineResponse(
                                        line.getId(),
                                        line.getLineNumber(),
                                        line.getAccountCode(),
                                        line.getAccountName(),
                                        line.getDirection(),
                                        line.getAmount()
                                )
                        )
                        .toList();


        AccountingEntryResponse
                accountingResponse =
                new AccountingEntryResponse(
                        entry.getId(),
                        entry.getEntryNumber(),
                        entry.getJournal().getCode(),
                        entry.getEntryDate(),
                        entry.getDescription(),
                        entry.getTotalDebit(),
                        entry.getTotalCredit(),
                        entry.getStatus(),
                        lineResponses
                );


        ExpenseRequest expense =
                payment.getExpenseRequest();


        return new ExpensePaymentResponse(
                payment.getId(),
                payment.getPaymentNumber(),
                expense.getId(),
                expense.getExpenseNumber(),
                expense.getSupplier() == null
                        ? null
                        : expense.getSupplier().getName(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentReference(),
                payment.getPaidAt(),
                payment.getPaidBy(),
                expense.getStatus(),
                commitment.getStatus(),
                budgetLine.getCommittedAmount(),
                budgetLine.getConsumedAmount(),
                budgetLine.getAvailableAmount(),
                treasury.getId(),
                treasury.getTransactionNumber(),
                accountingResponse
        );
    }


    private void validateReference(
            PayExpenseRequest request
    ) {

        if (
                request.paymentMethod()
                        == PaymentMethod.CASH
        ) {

            return;
        }


        if (
                request.paymentReference() == null
                || request.paymentReference()
                        .isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La reference de paiement est obligatoire hors paiement en especes."
            );
        }
    }


    private String treasuryAccountName(
            PaymentMethod method
    ) {

        return method == PaymentMethod.CASH
                ? "Caisse"
                : "Banque";
    }


    private String generateNumber(
            String prefix
    ) {

        String date =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.BASIC_ISO_DATE
                        );


        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase();


        return prefix
                + "-"
                + date
                + "-"
                + random;
    }


    private String currentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication == null
                || authentication.getName() == null
        ) {

            return "SYSTEM";
        }


        return authentication.getName();
    }


    private String clean(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String result =
                value.trim();

        return result.isEmpty()
                ? null
                : result;
    }
}