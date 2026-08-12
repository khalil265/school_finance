package com.schoolfinance.entity.budget;

import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "budget_lines",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_budget_line_code",
                        columnNames = {
                                "budget_id",
                                "code"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_budget_line_budget",
                        columnList = "budget_id"
                ),
                @Index(
                        name = "idx_budget_line_code",
                        columnList = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "budget_id",
            nullable = false
    )
    private Budget budget;

    @Column(
            name = "code",
            nullable = false,
            length = 80
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 200
    )
    private String name;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "allocated_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal allocatedAmount;

    @Column(
            name = "committed_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal committedAmount = BigDecimal.ZERO;

    @Column(
            name = "consumed_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal consumedAmount = BigDecimal.ZERO;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @Transient
    public BigDecimal getAvailableAmount() {

        return allocatedAmount
                .subtract(committedAmount)
                .subtract(consumedAmount);
    }
}