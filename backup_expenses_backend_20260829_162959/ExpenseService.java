package com.schoolfinance.service;

import com.schoolfinance.dto.expense.CreateExpenseRequest;
import com.schoolfinance.dto.expense.ExpenseResponse;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.expense.ExpenseCategory;
import com.schoolfinance.entity.expense.ExpenseRequest;
import com.schoolfinance.entity.expense.Supplier;
import com.schoolfinance.enums.ExpenseStatus;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.expense.ExpenseCategoryRepository;
import com.schoolfinance.repository.expense.ExpenseRequestRepository;
import com.schoolfinance.repository.expense.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRequestRepository expenseRepository;

    private final SupplierRepository supplierRepository;

    private final ExpenseCategoryRepository categoryRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AuditService auditService;


    @Transactional
    public ExpenseResponse create(
            CreateExpenseRequest request
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


        Supplier supplier = null;

        if (request.supplierId() != null) {

            supplier =
                    supplierRepository
                            .findById(request.supplierId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Fournisseur introuvable."
                                    )
                            );


            if (
                    !supplier
                            .getEstablishment()
                            .getId()
                            .equals(
                                    request.establishmentId()
                            )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le fournisseur n'appartient pas a cet etablissement."
                );
            }
        }


        ExpenseCategory category =
                categoryRepository
                        .findById(request.expenseCategoryId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Categorie de depense introuvable."
                                )
                        );

        if (
                !category
                        .getEstablishment()
                        .getId()
                        .equals(
                                request.establishmentId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categorie de depense n'appartient pas a cet etablissement."
            );
        }


        ExpenseRequest expense =
                ExpenseRequest.builder()
                        .establishment(establishment)
                        .supplier(supplier)
                        .category(category)
                        .expenseNumber(
                                generateExpenseNumber()
                        )
                        .subject(request.subject().trim())
                        .description(request.description())
                        .amount(request.amount())
                        .currency(
                                establishment.getCurrency()
                        )
                        .status(ExpenseStatus.DRAFT)
                        .requestedBy(currentUsername())
                        .build();


        expense =
                expenseRepository.save(
                        expense
                );


        ExpenseResponse response =
                toResponse(expense);


        auditService.log(
                "EXPENSE_CREATED",
                "ExpenseRequest",
                expense.getId(),
                null,
                response
        );


        return response;
    }


    @Transactional
    public ExpenseResponse submit(
            UUID id
    ) {

        ExpenseRequest expense =
                getEntity(id);


        requireStatus(
                expense,
                ExpenseStatus.DRAFT
        );


        expense.setStatus(
                ExpenseStatus.SUBMITTED
        );

        expense.setSubmittedAt(
                LocalDateTime.now()
        );


        expense =
                expenseRepository.save(
                        expense
                );


        auditService.log(
                "EXPENSE_SUBMITTED",
                "ExpenseRequest",
                expense.getId(),
                "DRAFT",
                "SUBMITTED"
        );


        return toResponse(expense);
    }


    @Transactional
    public ExpenseResponse verify(
            UUID id
    ) {

        ExpenseRequest expense =
                getEntity(id);


        requireStatus(
                expense,
                ExpenseStatus.SUBMITTED
        );


        expense.setStatus(
                ExpenseStatus.VERIFIED
        );

        expense.setVerifiedBy(
                currentUsername()
        );

        expense.setVerifiedAt(
                LocalDateTime.now()
        );


        expense =
                expenseRepository.save(
                        expense
                );


        auditService.log(
                "EXPENSE_VERIFIED",
                "ExpenseRequest",
                expense.getId(),
                "SUBMITTED",
                "VERIFIED"
        );


        return toResponse(expense);
    }


    @Transactional
    public ExpenseResponse approve(
            UUID id
    ) {

        ExpenseRequest expense =
                getEntity(id);


        requireStatus(
                expense,
                ExpenseStatus.BUDGET_CHECKED
        );


        expense.setStatus(
                ExpenseStatus.APPROVED
        );

        expense.setApprovedBy(
                currentUsername()
        );

        expense.setApprovedAt(
                LocalDateTime.now()
        );


        expense =
                expenseRepository.save(
                        expense
                );


        auditService.log(
                "EXPENSE_APPROVED",
                "ExpenseRequest",
                expense.getId(),
                "BUDGET_CHECKED",
                "APPROVED"
        );


        return toResponse(expense);
    }


    @Transactional
    public ExpenseResponse reject(
            UUID id,
            String reason
    ) {

        ExpenseRequest expense =
                getEntity(id);


        if (
                expense.getStatus()
                        != ExpenseStatus.SUBMITTED
                &&
                expense.getStatus()
                        != ExpenseStatus.VERIFIED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette depense ne peut plus etre rejetee."
            );
        }


        ExpenseStatus previous =
                expense.getStatus();


        expense.setStatus(
                ExpenseStatus.REJECTED
        );

        expense.setRejectedBy(
                currentUsername()
        );

        expense.setRejectedAt(
                LocalDateTime.now()
        );

        expense.setRejectionReason(
                reason
        );


        expense =
                expenseRepository.save(
                        expense
                );


        auditService.log(
                "EXPENSE_REJECTED",
                "ExpenseRequest",
                expense.getId(),
                previous.name(),
                "REJECTED : " + reason
        );


        return toResponse(expense);
    }


    @Transactional(readOnly = true)
    public ExpenseResponse get(
            UUID id
    ) {

        return toResponse(
                getEntity(id)
        );
    }


    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(
            UUID establishmentId
    ) {

        return expenseRepository
                .findByEstablishmentIdOrderByCreatedAtDesc(
                        establishmentId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private ExpenseRequest getEntity(
            UUID id
    ) {

        return expenseRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Depense introuvable."
                        )
                );
    }


    private void requireStatus(
            ExpenseRequest expense,
            ExpenseStatus expected
    ) {

        if (
                expense.getStatus()
                        != expected
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Statut attendu : "
                            + expected
                            + ", statut actuel : "
                            + expense.getStatus()
            );
        }
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


    private String generateExpenseNumber() {

        String date =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd"
                                )
                        );


        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase();


        return "EXP-"
                + date
                + "-"
                + random;
    }


    private ExpenseResponse toResponse(
            ExpenseRequest expense
    ) {

        Supplier supplier =
                expense.getSupplier();

        ExpenseCategory category =
                expense.getCategory();


        return new ExpenseResponse(
                expense.getId(),
                expense.getExpenseNumber(),
                expense.getEstablishment().getId(),
                supplier == null
                        ? null
                        : supplier.getId(),
                supplier == null
                        ? null
                        : supplier.getName(),
                category == null
                        ? null
                        : category.getId(),
                category == null
                        ? null
                        : category.getCode(),
                category == null
                        ? null
                        : category.getName(),
                expense.getSubject(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCurrency(),
                expense.getStatus(),
                expense.getRequestedBy(),
                expense.getSubmittedAt(),
                expense.getVerifiedBy(),
                expense.getVerifiedAt(),
                expense.getApprovedBy(),
                expense.getApprovedAt(),
                expense.getRejectedBy(),
                expense.getRejectedAt(),
                expense.getRejectionReason(),
                expense.getPaidAt(),
                expense.getPaymentReference()
        );
    }
}