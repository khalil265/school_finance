package com.schoolfinance.service;

import com.schoolfinance.dto.expense.SupplierRequest;
import com.schoolfinance.dto.expense.SupplierResponse;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.expense.Supplier;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.expense.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AuditService auditService;


    @Transactional
    public SupplierResponse create(
            SupplierRequest request
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


        if (
                supplierRepository
                        .existsByEstablishmentIdAndCode(
                                request.establishmentId(),
                                code
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce code fournisseur existe deja."
            );
        }


        Supplier supplier =
                Supplier.builder()
                        .establishment(establishment)
                        .code(code)
                        .name(request.name().trim())
                        .taxIdentifier(request.taxIdentifier())
                        .phone(request.phone())
                        .email(request.email())
                        .address(request.address())
                        .bankName(request.bankName())
                        .bankAccount(request.bankAccount())
                        .active(true)
                        .build();


        supplier =
                supplierRepository.save(
                        supplier
                );


        SupplierResponse response =
                toResponse(supplier);


        auditService.log(
                "SUPPLIER_CREATED",
                "Supplier",
                supplier.getId(),
                null,
                response
        );


        return response;
    }


    @Transactional(readOnly = true)
    public List<SupplierResponse> list(
            UUID establishmentId
    ) {

        return supplierRepository
                .findByEstablishmentIdOrderByNameAsc(
                        establishmentId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private SupplierResponse toResponse(
            Supplier supplier
    ) {

        return new SupplierResponse(
                supplier.getId(),
                supplier.getEstablishment().getId(),
                supplier.getCode(),
                supplier.getName(),
                supplier.getTaxIdentifier(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress(),
                supplier.getBankName(),
                supplier.getBankAccount(),
                supplier.getActive()
        );
    }
}