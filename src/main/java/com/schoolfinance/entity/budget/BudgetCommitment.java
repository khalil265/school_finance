package com.schoolfinance.entity.budget;

import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.entity.expense.ExpenseRequest;
import com.schoolfinance.enums.BudgetCommitmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "budget_commitments",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_commitment_expense",
                        columnNames = "expense_request_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_commitment_budget_line",
                        columnList = "budget_line_id"
                ),
                @Index(
                        name = "idx_commitment_expense",
                        columnList = "expense_request_id"
                ),
                @Index(
                        name = "idx_commitment_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetCommitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "budget_line_id",
            nullable = false
    )
    private BudgetLine budgetLine;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "expense_request_id",
            nullable = false,
            unique = true
    )
    private ExpenseRequest expenseRequest;

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
    private BudgetCommitmentStatus status =
            BudgetCommitmentStatus.RESERVED;

    @Column(
            name = "committed_at",
            nullable = false
    )
    private LocalDateTime committedAt;

    @Column(
            name = "committed_by",
            nullable = false,
            length = 150
    )
    private String committedBy;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(
            name = "release_reason",
            columnDefinition = "TEXT"
    )
    private String releaseReason;
}