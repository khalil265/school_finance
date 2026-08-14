package com.schoolfinance.service;

import com.schoolfinance.dto.bank.*;
import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.entity.accounting.AccountingEntryLine;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.bank.BankStatement;
import com.schoolfinance.entity.bank.BankStatementLine;
import com.schoolfinance.enums.*;
import com.schoolfinance.repository.accounting.AccountingEntryLineRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.bank.BankStatementLineRepository;
import com.schoolfinance.repository.bank.BankStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankReconciliationService {

    private final BankStatementRepository statementRepository;

    private final BankStatementLineRepository lineRepository;

    private final AccountingEntryLineRepository accountingLineRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AccountingAccountValidationService
            accountValidationService;

    private final AuditService auditService;


    @Transactional
    public BankStatementResponse createStatement(
            CreateBankStatementRequest request
    ) {

        if (
                request.endDate()
                        .isBefore(
                                request.startDate()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La date de fin doit etre posterieure ou egale a la date de debut."
            );
        }


        Establishment establishment =
                establishmentRepository
                        .findById(
                                request.establishmentId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );


        AccountingAccount account =
                accountValidationService
                        .requirePostingAccount(
                                establishment.getId(),
                                request.accountCode(),
                                AccountingAccountType.ASSET
                        );


        if (
                statementRepository
                        .existsByEstablishmentIdAndStatementReferenceIgnoreCase(
                                establishment.getId(),
                                request.statementReference()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un releve bancaire avec cette reference existe deja."
            );
        }


        BankStatement statement =
                BankStatement.builder()
                        .establishment(establishment)
                        .statementReference(
                                request.statementReference()
                                        .trim()
                        )
                        .bankName(
                                request.bankName()
                                        .trim()
                        )
                        .bankAccountNumber(
                                clean(
                                        request.bankAccountNumber()
                                )
                        )
                        .accountCode(
                                account.getCode()
                        )
                        .startDate(
                                request.startDate()
                        )
                        .endDate(
                                request.endDate()
                        )
                        .openingBalance(
                                request.openingBalance()
                        )
                        .closingBalance(
                                request.closingBalance()
                        )
                        .status(
                                BankStatementStatus.OPEN
                        )
                        .build();


        statement =
                statementRepository.save(
                        statement
                );


        auditService.log(
                "BANK_STATEMENT_CREATED",
                "BankStatement",
                statement.getId(),
                null,
                statement.getStatementReference()
        );


        return toStatementResponse(
                statement
        );
    }


    @Transactional
    public BankStatementLineResponse addLine(
            UUID statementId,
            CreateBankStatementLineRequest request
    ) {

        BankStatement statement =
                getStatement(
                        statementId
                );


        if (
                statement.getStatus()
                        != BankStatementStatus.OPEN
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le releve bancaire est cloture."
            );
        }


        if (
                request.transactionDate()
                        .isBefore(
                                statement.getStartDate()
                        )
                ||
                request.transactionDate()
                        .isAfter(
                                statement.getEndDate()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La date de transaction est hors periode du releve."
            );
        }


        BankStatementLine line =
                BankStatementLine.builder()
                        .bankStatement(statement)
                        .transactionDate(
                                request.transactionDate()
                        )
                        .bankReference(
                                clean(
                                        request.bankReference()
                                )
                        )
                        .description(
                                request.description()
                                        .trim()
                        )
                        .direction(
                                request.direction()
                        )
                        .amount(
                                request.amount()
                        )
                        .status(
                                BankStatementLineStatus.UNMATCHED
                        )
                        .build();


        line =
                lineRepository.save(
                        line
                );


        return toLineResponse(
                line
        );
    }


    @Transactional(readOnly = true)
    public List<BankStatementResponse> listStatements(
            UUID establishmentId
    ) {

        return statementRepository
                .findByEstablishmentIdOrderByStartDateDesc(
                        establishmentId
                )
                .stream()
                .map(this::toStatementResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<BankStatementLineResponse> getLines(
            UUID statementId
    ) {

        getStatement(statementId);


        return lineRepository
                .findByBankStatementIdOrderByTransactionDateAscCreatedAtAsc(
                        statementId
                )
                .stream()
                .map(this::toLineResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<BankReconciliationCandidateResponse>
    candidates(
            UUID statementLineId
    ) {

        BankStatementLine statementLine =
                getLine(
                        statementLineId
                );


        BankStatement statement =
                statementLine
                        .getBankStatement();


        LocalDateTime from =
                statementLine
                        .getTransactionDate()
                        .minusDays(5)
                        .atStartOfDay();


        LocalDateTime to =
                statementLine
                        .getTransactionDate()
                        .plusDays(5)
                        .atTime(
                                LocalTime.MAX
                        );


        AccountingDirection expectedDirection =
                statementLine.getDirection()
                        == BankStatementDirection.CREDIT
                        ? AccountingDirection.DEBIT
                        : AccountingDirection.CREDIT;


        return accountingLineRepository
                .findBankReconciliationCandidates(
                        statement
                                .getEstablishment()
                                .getId(),
                        statement.getAccountCode(),
                        from,
                        to
                )
                .stream()
                .filter(line ->
                        line.getDirection()
                                == expectedDirection
                )
                .filter(line ->
                        !lineRepository
                                .existsByAccountingEntryLineId(
                                        line.getId()
                                )
                )
                .map(line ->
                        new BankReconciliationCandidateResponse(
                                line.getId(),
                                line.getAccountingEntry()
                                        .getId(),
                                line.getAccountingEntry()
                                        .getEntryNumber(),
                                line.getAccountingEntry()
                                        .getEntryDate(),
                                line.getAccountCode(),
                                line.getAccountName(),
                                line.getDirection(),
                                line.getAmount(),
                                line.getDescription()
                        )
                )
                .toList();
    }


    @Transactional
    public BankStatementLineResponse reconcile(
            UUID statementLineId,
            ReconcileBankLineRequest request
    ) {

        BankStatementLine statementLine =
                getLine(
                        statementLineId
                );


        if (
                statementLine.getStatus()
                        != BankStatementLineStatus.UNMATCHED
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette ligne bancaire est deja traitee."
            );
        }


        if (
                lineRepository
                        .existsByAccountingEntryLineId(
                                request.accountingEntryLineId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette ligne comptable est deja rapprochee."
            );
        }


        AccountingEntryLine accountingLine =
                accountingLineRepository
                        .findById(
                                request.accountingEntryLineId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Ligne comptable introuvable."
                                )
                        );


        BankStatement statement =
                statementLine
                        .getBankStatement();


        if (
                !accountingLine
                        .getAccountingEntry()
                        .getEstablishment()
                        .getId()
                        .equals(
                                statement
                                        .getEstablishment()
                                        .getId()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ligne comptable appartient a un autre etablissement."
            );
        }


        if (
                !accountingLine
                        .getAccountCode()
                        .equalsIgnoreCase(
                                statement.getAccountCode()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ligne comptable n'utilise pas le compte bancaire du releve."
            );
        }


        AccountingDirection expectedDirection =
                statementLine.getDirection()
                        == BankStatementDirection.CREDIT
                        ? AccountingDirection.DEBIT
                        : AccountingDirection.CREDIT;


        if (
                accountingLine.getDirection()
                        != expectedDirection
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le sens de la ligne comptable ne correspond pas au mouvement bancaire."
            );
        }


        BigDecimal difference =
                statementLine
                        .getAmount()
                        .subtract(
                                accountingLine
                                        .getAmount()
                        );


        statementLine.setAccountingEntryLine(
                accountingLine
        );

        statementLine.setDifferenceAmount(
                difference
        );

        statementLine.setReconciledAt(
                LocalDateTime.now()
        );

        statementLine.setReconciledBy(
                currentUsername()
        );


        if (
                difference.compareTo(
                        BigDecimal.ZERO
                ) == 0
        ) {

            statementLine.setStatus(
                    BankStatementLineStatus.MATCHED
            );

        }
        else {

            statementLine.setStatus(
                    BankStatementLineStatus.DISCREPANCY
            );
        }


        statementLine =
                lineRepository.save(
                        statementLine
                );


        auditService.log(
                "BANK_LINE_RECONCILED",
                "BankStatementLine",
                statementLine.getId(),
                "UNMATCHED",
                statementLine.getStatus()
                        + " / difference="
                        + difference
        );


        return toLineResponse(
                statementLine
        );
    }


    @Transactional
    public BankStatementResponse closeStatement(
            UUID statementId
    ) {

        BankStatement statement =
                getStatement(
                        statementId
                );


        List<BankStatementLine> lines =
                lineRepository
                        .findByBankStatementIdOrderByTransactionDateAscCreatedAtAsc(
                                statementId
                        );


        boolean unresolved =
                lines.stream()
                        .anyMatch(line ->
                                line.getStatus()
                                        == BankStatementLineStatus.UNMATCHED
                        );


        if (unresolved) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le releve contient encore des lignes non rapprochees."
            );
        }


        statement.setStatus(
                BankStatementStatus.CLOSED
        );


        statement =
                statementRepository.save(
                        statement
                );


        auditService.log(
                "BANK_STATEMENT_CLOSED",
                "BankStatement",
                statement.getId(),
                "OPEN",
                "CLOSED"
        );


        return toStatementResponse(
                statement
        );
    }


    private BankStatement getStatement(
            UUID id
    ) {

        return statementRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Releve bancaire introuvable."
                        )
                );
    }


    private BankStatementLine getLine(
            UUID id
    ) {

        return lineRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Ligne de releve bancaire introuvable."
                        )
                );
    }


    private BankStatementResponse toStatementResponse(
            BankStatement statement
    ) {

        return new BankStatementResponse(
                statement.getId(),
                statement.getEstablishment()
                        .getId(),
                statement.getStatementReference(),
                statement.getBankName(),
                statement.getBankAccountNumber(),
                statement.getAccountCode(),
                statement.getStartDate(),
                statement.getEndDate(),
                statement.getOpeningBalance(),
                statement.getClosingBalance(),
                statement.getStatus()
        );
    }


    private BankStatementLineResponse toLineResponse(
            BankStatementLine line
    ) {

        AccountingEntryLine accountingLine =
                line.getAccountingEntryLine();


        return new BankStatementLineResponse(
                line.getId(),
                line.getBankStatement()
                        .getId(),
                line.getTransactionDate(),
                line.getBankReference(),
                line.getDescription(),
                line.getDirection(),
                line.getAmount(),
                line.getStatus(),

                accountingLine == null
                        ? null
                        : accountingLine.getId(),

                accountingLine == null
                        ? null
                        : accountingLine
                                .getAccountingEntry()
                                .getEntryNumber(),

                accountingLine == null
                        ? null
                        : accountingLine
                                .getAccountCode(),

                accountingLine == null
                        ? null
                        : accountingLine
                                .getAmount(),

                line.getDifferenceAmount(),
                line.getReconciledAt(),
                line.getReconciledBy()
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