package com.schoolfinance.entity.bank;

import com.schoolfinance.entity.accounting.AccountingEntryLine;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.BankStatementDirection;
import com.schoolfinance.enums.BankStatementLineStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "bank_statement_lines",
        schema = "school_finance",
        indexes = {
                @Index(
                        name = "idx_bank_statement_line_statement",
                        columnList = "bank_statement_id"
                ),
                @Index(
                        name = "idx_bank_statement_line_date",
                        columnList = "transaction_date"
                ),
                @Index(
                        name = "idx_bank_statement_line_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankStatementLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "bank_statement_id",
            nullable = false
    )
    private BankStatement bankStatement;

    @Column(
            name = "transaction_date",
            nullable = false
    )
    private LocalDate transactionDate;

    @Column(
            name = "bank_reference",
            length = 150
    )
    private String bankReference;

    @Column(
            name = "description",
            nullable = false,
            length = 300
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "direction",
            nullable = false,
            length = 20
    )
    private BankStatementDirection direction;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private BankStatementLineStatus status =
            BankStatementLineStatus.UNMATCHED;

    /**
     * Ligne comptable rapprochee.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accounting_entry_line_id",
            unique = true
    )
    private AccountingEntryLine accountingEntryLine;

    @Column(
            name = "difference_amount",
            precision = 19,
            scale = 2
    )
    private BigDecimal differenceAmount;

    @Column(
            name = "reconciled_at"
    )
    private LocalDateTime reconciledAt;

    @Column(
            name = "reconciled_by",
            length = 150
    )
    private String reconciledBy;
}