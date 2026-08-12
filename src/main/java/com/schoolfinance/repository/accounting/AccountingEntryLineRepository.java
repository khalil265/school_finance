package com.schoolfinance.repository.accounting;

import com.schoolfinance.entity.accounting.AccountingEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AccountingEntryLineRepository
        extends JpaRepository<AccountingEntryLine, UUID> {

    List<AccountingEntryLine>
    findByAccountingEntryIdOrderByLineNumberAsc(
            UUID accountingEntryId
    );


    // ============================================================
    // JOURNAL GENERAL - SANS FILTRE DE DATE
    // ============================================================

    @Query("""
            select l
            from AccountingEntryLine l
            join fetch l.accountingEntry e
            join fetch e.journal j
            where e.establishment.id = :establishmentId
              and e.status = com.schoolfinance.enums.AccountingEntryStatus.POSTED
            order by e.entryDate asc, e.entryNumber asc, l.lineNumber asc
            """)
    List<AccountingEntryLine> findPostedJournalLines(
            @Param("establishmentId")
            UUID establishmentId
    );


    // ============================================================
    // JOURNAL GENERAL - AVEC INTERVALLE DE DATE
    // ============================================================

    @Query("""
            select l
            from AccountingEntryLine l
            join fetch l.accountingEntry e
            join fetch e.journal j
            where e.establishment.id = :establishmentId
              and e.status = com.schoolfinance.enums.AccountingEntryStatus.POSTED
              and e.entryDate >= :fromDate
              and e.entryDate <= :toDate
            order by e.entryDate asc, e.entryNumber asc, l.lineNumber asc
            """)
    List<AccountingEntryLine> findPostedJournalLinesBetween(
            @Param("establishmentId")
            UUID establishmentId,

            @Param("fromDate")
            LocalDateTime fromDate,

            @Param("toDate")
            LocalDateTime toDate
    );


    // ============================================================
    // GRAND LIVRE - SANS FILTRE DE DATE
    // ============================================================

    @Query("""
            select l
            from AccountingEntryLine l
            join fetch l.accountingEntry e
            join fetch e.journal j
            where e.establishment.id = :establishmentId
              and upper(l.accountCode) = upper(:accountCode)
              and e.status = com.schoolfinance.enums.AccountingEntryStatus.POSTED
            order by e.entryDate asc, e.entryNumber asc, l.lineNumber asc
            """)
    List<AccountingEntryLine> findPostedLedgerLines(
            @Param("establishmentId")
            UUID establishmentId,

            @Param("accountCode")
            String accountCode
    );


    // ============================================================
    // GRAND LIVRE - AVEC INTERVALLE DE DATE
    // ============================================================

    @Query("""
            select l
            from AccountingEntryLine l
            join fetch l.accountingEntry e
            join fetch e.journal j
            where e.establishment.id = :establishmentId
              and upper(l.accountCode) = upper(:accountCode)
              and e.status = com.schoolfinance.enums.AccountingEntryStatus.POSTED
              and e.entryDate >= :fromDate
              and e.entryDate <= :toDate
            order by e.entryDate asc, e.entryNumber asc, l.lineNumber asc
            """)
    List<AccountingEntryLine> findPostedLedgerLinesBetween(
            @Param("establishmentId")
            UUID establishmentId,

            @Param("accountCode")
            String accountCode,

            @Param("fromDate")
            LocalDateTime fromDate,

            @Param("toDate")
            LocalDateTime toDate
    );
}