package com.schoolfinance.entity.finance;

import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "payment_allocations",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_payment_charge_allocation",
                        columnNames = {
                                "payment_id",
                                "student_charge_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_allocation_payment",
                        columnList = "payment_id"
                ),
                @Index(
                        name = "idx_allocation_charge",
                        columnList = "student_charge_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAllocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "payment_id",
            nullable = false
    )
    private Payment payment;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_charge_id",
            nullable = false
    )
    private StudentCharge studentCharge;

    @Column(
            name = "allocated_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal allocatedAmount;
}