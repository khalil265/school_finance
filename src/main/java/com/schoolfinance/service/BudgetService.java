package com.schoolfinance.service;

import com.schoolfinance.dto.budget.*;
import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.budget.Budget;
import com.schoolfinance.entity.budget.BudgetCommitment;
import com.schoolfinance.entity.budget.BudgetLine;
import com.schoolfinance.entity.expense.ExpenseRequest;
import com.schoolfinance.enums.BudgetCommitmentStatus;
import com.schoolfinance.enums.BudgetStatus;
import com.schoolfinance.enums.ExpenseStatus;
import com.schoolfinance.repository.administration.AcademicYearRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.budget.BudgetCommitmentRepository;
import com.schoolfinance.repository.budget.BudgetLineRepository;
import com.schoolfinance.repository.budget.BudgetRepository;
import com.schoolfinance.repository.expense.ExpenseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final BudgetLineRepository budgetLineRepository;

    private final BudgetCommitmentRepository commitmentRepository;

    private final ExpenseRequestRepository expenseRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AcademicYearRepository academicYearRepository;

    private final AuditService auditService;


    @Transactional
    public BudgetResponse createBudget(
            CreateBudgetRequest request
    ) {

        Establishment establishment =
                establishmentRepository
                        .findById(request.establishmentId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );


        AcademicYear academicYear =
                academicYearRepository
                        .findById(request.academicYearId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Annee academique introuvable."
                                )
                        );


        if (
                !academicYear
                        .getEstablishment()
                        .getId()
                        .equals(
                                establishment.getId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'annee academique ne correspond pas a l'etablissement."
            );
        }


        if (
                budgetRepository
                        .existsByEstablishmentIdAndAcademicYearId(
                                establishment.getId(),
                                academicYear.getId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un budget existe deja pour cette annee academique."
            );
        }


        Budget budget =
                Budget.builder()
                        .establishment(establishment)
                        .academicYear(academicYear)
                        .code(
                                request.code()
                                        .trim()
                                        .toUpperCase()
                        )
                        .name(
                                request.name().trim()
                        )
                        .description(
                                request.description()
                        )
                        .totalAmount(
                                BigDecimal.ZERO
                        )
                        .totalCommitted(
                                BigDecimal.ZERO
                        )
                        .totalConsumed(
                                BigDecimal.ZERO
                        )
                        .status(
                                BudgetStatus.DRAFT
                        )
                        .build();


        budget =
                budgetRepository.save(
                        budget
                );


        BudgetResponse response =
                toBudgetResponse(
                        budget
                );


        auditService.log(
                "BUDGET_CREATED",
                "Budget",
                budget.getId(),
                null,
                response
        );


        return response;
    }


    @Transactional
    public BudgetLineResponse addLine(
            UUID budgetId,
            CreateBudgetLineRequest request
    ) {

        Budget budget =
                getBudget(
                        budgetId
                );


        if (
                budget.getStatus()
                        != BudgetStatus.DRAFT
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Les lignes ne peuvent etre ajoutees qu'a un budget DRAFT."
            );
        }


        String code =
                request.code()
                        .trim()
                        .toUpperCase();


        if (
                budgetLineRepository
                        .existsByBudgetIdAndCodeIgnoreCase(
                                budgetId,
                                code
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette ligne budgetaire existe deja."
            );
        }


        BudgetLine line =
                BudgetLine.builder()
                        .budget(budget)
                        .code(code)
                        .name(
                                request.name().trim()
                        )
                        .description(
                                request.description()
                        )
                        .allocatedAmount(
                                request.allocatedAmount()
                        )
                        .committedAmount(
                                BigDecimal.ZERO
                        )
                        .consumedAmount(
                                BigDecimal.ZERO
                        )
                        .active(true)
                        .build();


        line =
                budgetLineRepository.save(
                        line
                );


        refreshBudgetTotals(
                budget
        );


        BudgetLineResponse response =
                toLineResponse(
                        line
                );


        auditService.log(
                "BUDGET_LINE_CREATED",
                "BudgetLine",
                line.getId(),
                null,
                response
        );


        return response;
    }


    @Transactional
    public BudgetResponse activate(
            UUID budgetId
    ) {

        Budget budget =
                getBudget(
                        budgetId
                );


        if (
                budget.getStatus()
                        != BudgetStatus.DRAFT
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Seul un budget DRAFT peut etre active."
            );
        }


        List<BudgetLine> lines =
                budgetLineRepository
                        .findByBudgetIdAndActiveTrueOrderByCodeAsc(
                                budgetId
                        );


        if (lines.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le budget doit contenir au moins une ligne."
            );
        }


        refreshBudgetTotals(
                budget
        );


        budget.setStatus(
                BudgetStatus.ACTIVE
        );


        budget =
                budgetRepository.save(
                        budget
                );


        auditService.log(
                "BUDGET_ACTIVATED",
                "Budget",
                budget.getId(),
                "DRAFT",
                "ACTIVE"
        );


        return toBudgetResponse(
                budget
        );
    }


    @Transactional(readOnly = true)
    public BudgetResponse get(
            UUID id
    ) {

        return toBudgetResponse(
                getBudget(id)
        );
    }


    @Transactional(readOnly = true)
    public List<BudgetResponse> list(
            UUID establishmentId
    ) {

        return budgetRepository
                .findByEstablishmentIdOrderByCreatedAtDesc(
                        establishmentId
                )
                .stream()
                .map(this::toBudgetResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<BudgetLineResponse> lines(
            UUID budgetId
    ) {

        getBudget(budgetId);

        return budgetLineRepository
                .findByBudgetIdAndActiveTrueOrderByCodeAsc(
                        budgetId
                )
                .stream()
                .map(this::toLineResponse)
                .toList();
    }


    @Transactional
    public BudgetCommitmentResponse checkAndCommit(
            UUID expenseId,
            UUID budgetLineId
    ) {

        ExpenseRequest expense =
                expenseRepository
                        .findById(expenseId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Depense introuvable."
                                )
                        );


        if (
                expense.getStatus()
                        != ExpenseStatus.VERIFIED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La depense doit etre VERIFIED avant le controle budgetaire."
            );
        }


        if (
                commitmentRepository
                        .existsByExpenseRequestId(
                                expenseId
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un engagement budgetaire existe deja pour cette depense."
            );
        }


        BudgetLine line =
                budgetLineRepository
                        .findByIdForUpdate(
                                budgetLineId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Ligne budgetaire introuvable."
                                )
                        );


        Budget budget =
                line.getBudget();


        if (
                budget.getStatus()
                        != BudgetStatus.ACTIVE
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le budget n'est pas actif."
            );
        }


        if (
                !budget
                        .getEstablishment()
                        .getId()
                        .equals(
                                expense
                                        .getEstablishment()
                                        .getId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ligne budgetaire et la depense appartiennent a des etablissements differents."
            );
        }


        BigDecimal available =
                line.getAvailableAmount();


        if (
                expense.getAmount()
                        .compareTo(
                                available
                        ) > 0
        ) {

            auditService.log(
                    "BUDGET_CHECK_REJECTED",
                    "ExpenseRequest",
                    expense.getId(),
                    null,
                    "Required="
                            + expense.getAmount()
                            + ", Available="
                            + available
            );


            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Budget insuffisant. Disponible : "
                            + available
                            + " "
                            + expense.getCurrency()
            );
        }


        line.setCommittedAmount(
                line.getCommittedAmount()
                        .add(
                                expense.getAmount()
                        )
        );


        budgetLineRepository.save(
                line
        );


        BudgetCommitment commitment =
                BudgetCommitment.builder()
                        .budgetLine(line)
                        .expenseRequest(expense)
                        .amount(
                                expense.getAmount()
                        )
                        .status(
                                BudgetCommitmentStatus.RESERVED
                        )
                        .committedAt(
                                LocalDateTime.now()
                        )
                        .committedBy(
                                currentUsername()
                        )
                        .build();


        commitment =
                commitmentRepository.save(
                        commitment
                );


        expense.setStatus(
                ExpenseStatus.BUDGET_CHECKED
        );


        expenseRepository.save(
                expense
        );


        refreshBudgetTotals(
                budget
        );


        auditService.log(
                "BUDGET_COMMITMENT_CREATED",
                "BudgetCommitment",
                commitment.getId(),
                null,
                "Expense="
                        + expense.getExpenseNumber()
                        + ", Amount="
                        + expense.getAmount()
                        + ", Line="
                        + line.getCode()
        );


        return new BudgetCommitmentResponse(
                commitment.getId(),
                expense.getId(),
                expense.getExpenseNumber(),
                line.getId(),
                line.getCode(),
                line.getName(),
                expense.getAmount(),
                commitment.getAmount(),
                line.getAvailableAmount(),
                commitment.getStatus(),
                expense.getStatus(),
                commitment.getCommittedBy(),
                commitment.getCommittedAt()
        );
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


        budget.setTotalAmount(
                total
        );

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


    private Budget getBudget(
            UUID id
    ) {

        return budgetRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Budget introuvable."
                        )
                );
    }


    private BudgetResponse toBudgetResponse(
            Budget budget
    ) {

        BigDecimal available =
                budget.getTotalAmount()
                        .subtract(
                                budget.getTotalCommitted()
                        )
                        .subtract(
                                budget.getTotalConsumed()
                        );


        return new BudgetResponse(
                budget.getId(),
                budget.getEstablishment().getId(),
                budget.getAcademicYear().getId(),
                budget.getAcademicYear().getLabel(),
                budget.getCode(),
                budget.getName(),
                budget.getDescription(),
                budget.getTotalAmount(),
                budget.getTotalCommitted(),
                budget.getTotalConsumed(),
                available,
                budget.getStatus()
        );
    }


    private BudgetLineResponse toLineResponse(
            BudgetLine line
    ) {

        return new BudgetLineResponse(
                line.getId(),
                line.getBudget().getId(),
                line.getCode(),
                line.getName(),
                line.getDescription(),
                line.getAllocatedAmount(),
                line.getCommittedAmount(),
                line.getConsumedAmount(),
                line.getAvailableAmount(),
                line.getActive()
        );
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
}