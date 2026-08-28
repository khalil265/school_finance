package com.schoolfinance.service;

import com.schoolfinance.dto.expense.ExpenseCategoryRequest;
import com.schoolfinance.dto.expense.ExpenseCategoryResponse;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.expense.ExpenseCategory;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.expense.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    private final EstablishmentRepository establishmentRepository;


    @Transactional
    public ExpenseCategoryResponse create(
            ExpenseCategoryRequest request
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

        String code =
                request.code()
                        .trim()
                        .toUpperCase();

        if (categoryRepository
                .existsByEstablishmentIdAndCodeIgnoreCase(
                        establishment.getId(),
                        code
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une categorie de depense avec ce code existe deja."
            );
        }

        ExpenseCategory category =
                ExpenseCategory.builder()
                        .establishment(establishment)
                        .code(code)
                        .name(request.name().trim())
                        .description(request.description())
                        .active(true)
                        .build();

        return toResponse(
                categoryRepository.save(category)
        );
    }


    @Transactional(readOnly = true)
    public List<ExpenseCategoryResponse> list(
            UUID establishmentId
    ) {

        return categoryRepository
                .findByEstablishmentIdAndActiveTrueOrderByNameAsc(
                        establishmentId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private ExpenseCategoryResponse toResponse(
            ExpenseCategory category
    ) {

        return new ExpenseCategoryResponse(
                category.getId(),
                category.getEstablishment().getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.getActive()
        );
    }
}