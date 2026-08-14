package com.schoolfinance.entity.bank;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.BankStatementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "bank_statements",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_bank_statement_reference",
                        columnNames = {
                                "establishment_id",
                                "statement_reference"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_bank_statement_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_bank_statement_period",
                        columnList = "start_date,end_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankStatement extends BaseEntity {

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

    @Column(
            name = "statement_reference",
            nullable = false,
            length = 100
    )
    private String statementReference;

    @Column(
            name = "bank_name",
            nullable = false,
            length = 150
    )
    private String bankName;

    @Column(
            name = "bank_account_number",
            length = 100
    )
    private String bankAccountNumber;

    @Column(
            name = "account_code",
            nullable = false,
            length = 30
    )
    private String accountCode;

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;

    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;

    @Column(
            name = "opening_balance",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal openingBalance;

    @Column(
            name = "closing_balance",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal closingBalance;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private BankStatementStatus status =
            BankStatementStatus.OPEN;
}