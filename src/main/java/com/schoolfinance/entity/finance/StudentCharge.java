package com.schoolfinance.entity.finance;

import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.ChargeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "student_charges",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_charge_installment",
                        columnNames = {
                                "student_account_id",
                                "fee_structure_id",
                                "installment_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_charge_account",
                        columnList = "student_account_id"
                ),
                @Index(
                        name = "idx_charge_fee_structure",
                        columnList = "fee_structure_id"
                ),
                @Index(
                        name = "idx_charge_due_date",
                        columnList = "due_date"
                ),
                @Index(
                        name = "idx_charge_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCharge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_account_id",
            nullable = false
    )
    private StudentAccount studentAccount;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "fee_structure_id",
            nullable = false
    )
    private FeeStructure feeStructure;

    @Column(
            name = "installment_number",
            nullable = false
    )
    private Integer installmentNumber;

    @Column(
            name = "label",
            nullable = false,
            length = 200
    )
    private String label;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "paid_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(
            name = "remaining_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal remainingAmount;

    @Column(
            name = "due_date"
    )
    private LocalDate dueDate;

    @Column(
            name = "grace_period_days",
            nullable = false
    )
    @Builder.Default
    private Integer gracePeriodDays = 0;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ChargeStatus status = ChargeStatus.PENDING;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}