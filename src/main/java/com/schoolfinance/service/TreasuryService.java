package com.schoolfinance.service;

import com.schoolfinance.dto.treasury.TreasuryTransactionResponse;
import com.schoolfinance.entity.treasury.TreasuryTransaction;
import com.schoolfinance.enums.TreasuryTransactionType;
import com.schoolfinance.repository.treasury.TreasuryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreasuryService {

    private final TreasuryTransactionRepository repository;


    @Transactional(readOnly = true)
    public List<TreasuryTransactionResponse> list(
            UUID establishmentId
    ) {

        return repository
                .findByEstablishmentIdOrderByTransactionDateDesc(
                        establishmentId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public BigDecimal balance(
            UUID establishmentId
    ) {

        return repository
                .findByEstablishmentIdOrderByTransactionDateDesc(
                        establishmentId
                )
                .stream()
                .map(t ->
                        t.getTransactionType() == TreasuryTransactionType.INCOME
                                ? t.getAmount()
                                : t.getAmount().negate()
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private TreasuryTransactionResponse toResponse(
            TreasuryTransaction transaction
    ) {

        return new TreasuryTransactionResponse(
                transaction.getId(),
                transaction.getTransactionNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getPaymentMethod(),
                transaction.getAccountCode(),
                transaction.getExternalReference(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getCreatedBy()
        );
    }
}