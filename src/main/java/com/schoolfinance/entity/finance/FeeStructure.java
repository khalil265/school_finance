package com.schoolfinance.entity.finance;

import com.schoolfinance.entity.academic.Level;
import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "fee_structures",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_fee_structure",
                        columnNames = {
                                "establishment_id",
                                "academic_year_id",
                                "level_id",
                                "fee_type_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_fee_structure_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_fee_structure_year",
                        columnList = "academic_year_id"
                ),
                @Index(
                        name = "idx_fee_structure_level",
                        columnList = "level_id"
                ),
                @Index(
                        name = "idx_fee_structure_type",
                        columnList = "fee_type_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructure extends BaseEntity {

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
            name = "level_id",
            nullable = false
    )
    private Level level;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "fee_type_id",
            nullable = false
    )
    private FeeType feeType;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "installment_count",
            nullable = false
    )
    @Builder.Default
    private Integer installmentCount = 1;

    @Column(
            name = "first_due_date"
    )
    private LocalDate firstDueDate;

    @Column(
            name = "grace_period_days",
            nullable = false
    )
    @Builder.Default
    private Integer gracePeriodDays = 0;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}