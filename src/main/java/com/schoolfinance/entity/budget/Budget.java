package com.schoolfinance.entity.budget;

import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.BudgetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "budgets",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_budget_establishment_year",
                        columnNames = {
                                "establishment_id",
                                "academic_year_id"
                        }
                ),
                @UniqueConstraint(
                        name = "uq_budget_code",
                        columnNames = "code"
                )
        },
        indexes = {
                @Index(
                        name = "idx_budget_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_budget_academic_year",
                        columnList = "academic_year_id"
                ),
                @Index(
                        name = "idx_budget_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends BaseEntity {

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

    @Column(
            name = "code",
            nullable = false,
            unique = true,
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
            name = "total_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(
            name = "total_committed",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal totalCommitted = BigDecimal.ZERO;

    @Column(
            name = "total_consumed",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal totalConsumed = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private BudgetStatus status = BudgetStatus.DRAFT;
}