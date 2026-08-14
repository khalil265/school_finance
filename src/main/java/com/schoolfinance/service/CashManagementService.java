package com.schoolfinance.service;

import com.schoolfinance.dto.cash.*;
import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.entity.accounting.AccountingEntryLine;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.cash.CashSession;
import com.schoolfinance.enums.*;
import com.schoolfinance.repository.accounting.AccountingEntryLineRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.cash.CashSessionRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class
CashManagementService {

    private final CashSessionRepository cashSessionRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AccountingEntryLineRepository accountingLineRepository;

    private final AccountingAccountValidationService
            accountValidationService;

    private final AuditService auditService;


    // ============================================================
    // OPEN SESSION
    // ============================================================

    @Transactional
    public CashSessionResponse open(
            OpenCashSessionRequest request
    ) {

        String accountCode =
                request.accountCode() == null
                        || request.accountCode().isBlank()
                        ? "571000"
                        : request.accountCode()
                                .trim()
                                .toUpperCase();


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


        AccountingAccount cashAccount =
                accountValidationService
                        .requirePostingAccount(
                                establishment.getId(),
                                accountCode,
                                AccountingAccountType.ASSET
                        );


        if (
                cashSessionRepository
                        .existsByEstablishmentIdAndAccountCodeIgnoreCaseAndStatus(
                                establishment.getId(),
                                cashAccount.getCode(),
                                CashSessionStatus.OPEN
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une session de caisse est deja ouverte pour ce compte."
            );
        }


        CashSession session =
                CashSession.builder()
                        .establishment(establishment)
                        .sessionNumber(
                                generateSessionNumber()
                        )
                        .accountCode(
                                cashAccount.getCode()
                        )
                        .openingBalance(
                                request.openingBalance()
                        )
                        .openedAt(
                                LocalDateTime.now()
                        )
                        .openedBy(
                                currentUsername()
                        )
                        .status(
                                CashSessionStatus.OPEN
                        )
                        .totalInflows(
                                BigDecimal.ZERO
                        )
                        .totalOutflows(
                                BigDecimal.ZERO
                        )
                        .theoreticalBalance(
                                request.openingBalance()
                        )
                        .build();


        session =
                cashSessionRepository.save(
                        session
                );


        auditService.log(
                "CASH_SESSION_OPENED",
                "CashSession",
                session.getId(),
                null,
                session.getSessionNumber()
        );


        return calculateAndMap(
                session,
                LocalDateTime.now()
        ).session();
    }


    // ============================================================
    // CURRENT SESSION
    // ============================================================

    @Transactional(readOnly = true)
    public CashSessionDetailsResponse current(
            UUID establishmentId,
            String accountCode
    ) {

        String normalizedCode =
                normalizeAccountCode(
                        accountCode
                );


        CashSession session =
                cashSessionRepository
                        .findFirstByEstablishmentIdAndAccountCodeIgnoreCaseAndStatusOrderByOpenedAtDesc(
                                establishmentId,
                                normalizedCode,
                                CashSessionStatus.OPEN
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Aucune session de caisse ouverte."
                                )
                        );


        return calculateAndMap(
                session,
                LocalDateTime.now()
        );
    }


    // ============================================================
    // GET ONE SESSION
    // ============================================================

    @Transactional(readOnly = true)
    public CashSessionDetailsResponse get(
            UUID sessionId
    ) {

        CashSession session =
                getSession(
                        sessionId
                );


        LocalDateTime end =
                session.getClosedAt() == null
                        ? LocalDateTime.now()
                        : session.getClosedAt();


        return calculateAndMap(
                session,
                end
        );
    }


    // ============================================================
    // LIST SESSIONS
    // ============================================================

    @Transactional(readOnly = true)
    public List<CashSessionResponse> list(
            UUID establishmentId
    ) {

        return cashSessionRepository
                .findByEstablishmentIdOrderByOpenedAtDesc(
                        establishmentId
                )
                .stream()
                .map(session -> {

                    LocalDateTime end =
                            session.getClosedAt() == null
                                    ? LocalDateTime.now()
                                    : session.getClosedAt();


                    return calculateAndMap(
                            session,
                            end
                    ).session();
                })
                .toList();
    }


    // ============================================================
    // CLOSE SESSION
    // ============================================================

    @Transactional
    public CashSessionDetailsResponse close(
            UUID sessionId,
            CloseCashSessionRequest request
    ) {

        CashSession session =
                getSession(
                        sessionId
                );


        if (
                session.getStatus()
                        != CashSessionStatus.OPEN
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette session de caisse est deja cloturee."
            );
        }


        LocalDateTime now =
                LocalDateTime.now();


        CashSessionDetailsResponse calculated =
                calculateAndMap(
                        session,
                        now
                );


        CashSessionResponse snapshot =
                calculated.session();


        BigDecimal difference =
                request.physicalBalance()
                        .subtract(
                                snapshot.theoreticalBalance()
                        );


        session.setTotalInflows(
                snapshot.totalInflows()
        );

        session.setTotalOutflows(
                snapshot.totalOutflows()
        );

        session.setTheoreticalBalance(
                snapshot.theoreticalBalance()
        );

        session.setPhysicalBalance(
                request.physicalBalance()
        );

        session.setDifferenceAmount(
                difference
        );

        session.setClosedAt(now);

        session.setClosedBy(
                currentUsername()
        );

        session.setClosingNotes(
                clean(
                        request.notes()
                )
        );

        session.setStatus(
                CashSessionStatus.CLOSED
        );


        session =
                cashSessionRepository.save(
                        session
                );


        auditService.log(
                "CASH_SESSION_CLOSED",
                "CashSession",
                session.getId(),
                "OPEN",
                "CLOSED / difference="
                        + difference
        );


        return calculateAndMap(
                session,
                now
        );
    }


    // ============================================================
    // CALCULATION ENGINE
    // ============================================================

    private CashSessionDetailsResponse calculateAndMap(
            CashSession session,
            LocalDateTime end
    ) {

        List<AccountingEntryLine> accountingLines =
                accountingLineRepository
                        .findPostedLedgerLinesBetween(
                                session.getEstablishment()
                                        .getId(),
                                session.getAccountCode(),
                                session.getOpenedAt(),
                                end
                        );


        BigDecimal totalInflows =
                BigDecimal.ZERO;

        BigDecimal totalOutflows =
                BigDecimal.ZERO;

        BigDecimal runningBalance =
                session.getOpeningBalance();


        List<CashMovementResponse> movements =
                new ArrayList<>();


        for (
                AccountingEntryLine line
                : accountingLines
        ) {

            BigDecimal inflow =
                    line.getDirection()
                            == AccountingDirection.DEBIT
                            ? line.getAmount()
                            : BigDecimal.ZERO;


            BigDecimal outflow =
                    line.getDirection()
                            == AccountingDirection.CREDIT
                            ? line.getAmount()
                            : BigDecimal.ZERO;


            totalInflows =
                    totalInflows.add(
                            inflow
                    );


            totalOutflows =
                    totalOutflows.add(
                            outflow
                    );


            runningBalance =
                    runningBalance
                            .add(inflow)
                            .subtract(outflow);


            movements.add(
                    new CashMovementResponse(
                            line.getId(),
                            line.getAccountingEntry()
                                    .getId(),
                            line.getAccountingEntry()
                                    .getEntryNumber(),
                            line.getAccountingEntry()
                                    .getEntryDate(),
                            line.getAccountingEntry()
                                    .getJournal()
                                    .getCode(),
                            line.getDescription(),
                            line.getAccountCode(),
                            line.getDirection(),
                            inflow,
                            outflow,
                            runningBalance
                    )
            );
        }


        BigDecimal theoreticalBalance =
                session.getOpeningBalance()
                        .add(totalInflows)
                        .subtract(totalOutflows);


        BigDecimal physicalBalance =
                session.getPhysicalBalance();


        BigDecimal difference =
                physicalBalance == null
                        ? null
                        : physicalBalance
                                .subtract(
                                        theoreticalBalance
                                );


        CashSessionResponse response =
                new CashSessionResponse(
                        session.getId(),
                        session.getEstablishment()
                                .getId(),
                        session.getSessionNumber(),
                        session.getAccountCode(),
                        session.getStatus(),
                        session.getOpeningBalance(),
                        totalInflows,
                        totalOutflows,
                        theoreticalBalance,
                        physicalBalance,
                        difference,
                        session.getOpenedAt(),
                        session.getOpenedBy(),
                        session.getClosedAt(),
                        session.getClosedBy(),
                        session.getClosingNotes()
                );


        return new CashSessionDetailsResponse(
                response,
                movements
        );
    }


    // ============================================================
    // HELPERS
    // ============================================================

    private CashSession getSession(
            UUID id
    ) {

        return cashSessionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Session de caisse introuvable."
                        )
                );
    }


    private String normalizeAccountCode(
            String accountCode
    ) {

        if (
                accountCode == null
                || accountCode.isBlank()
        ) {

            return "571000";
        }


        return accountCode
                .trim()
                .toUpperCase();
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


    private String generateSessionNumber() {

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


        return "CASH-"
                + date
                + "-"
                + random;
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