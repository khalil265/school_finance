package com.schoolfinance.entity.accounting;

import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.AccountingDirection;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "accounting_entry_lines",
        schema = "school_finance",
        indexes = {
                @Index(
                        name = "idx_entry_line_entry",
                        columnList = "accounting_entry_id"
                ),
                @Index(
                        name = "idx_entry_line_account",
                        columnList = "account_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingEntryLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "accounting_entry_id",
            nullable = false
    )
    private AccountingEntry accountingEntry;

    @Column(
            name = "line_number",
            nullable = false
    )
    private Integer lineNumber;

    @Column(
            name = "account_code",
            nullable = false,
            length = 80
    )
    private String accountCode;

    @Column(
            name = "account_name",
            nullable = false,
            length = 200
    )
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "direction",
            nullable = false,
            length = 20
    )
    private AccountingDirection direction;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "description",
            length = 300
    )
    private String description;
}