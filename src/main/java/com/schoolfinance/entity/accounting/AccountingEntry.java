package com.schoolfinance.entity.accounting;

import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.entity.expense.ExpensePayment;
import com.schoolfinance.enums.AccountingEntryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "accounting_entries",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_accounting_entry_number",
                        columnNames = "entry_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_accounting_entry_date",
                        columnList = "entry_date"
                ),
                @Index(
                        name = "idx_accounting_entry_establishment",
                        columnList = "establishment_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "establishment_id",
            nullable = false
    )
    private Establishment establishment;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "academic_year_id",
            nullable = false
    )
    private AcademicYear academicYear;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "journal_id",
            nullable = false
    )
    private AccountingJournal journal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expense_payment_id",
            unique = true
    )
    private ExpensePayment expensePayment;

    @Column(
            name = "entry_number",
            nullable = false,
            unique = true,
            length = 80
    )
    private String entryNumber;

    @Column(
            name = "entry_date",
            nullable = false
    )
    private LocalDateTime entryDate;

    @Column(
            name = "description",
            nullable = false,
            length = 300
    )
    private String description;

    @Column(
            name = "total_debit",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal totalDebit;

    @Column(
            name = "total_credit",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal totalCredit;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private AccountingEntryStatus status;

    @Column(
            name = "posted_by",
            nullable = false,
            length = 150
    )
    private String postedBy;

    @Column(
            name = "posted_at",
            nullable = false
    )
    private LocalDateTime postedAt;
}