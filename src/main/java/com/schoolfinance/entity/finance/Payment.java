package com.schoolfinance.entity.finance;

import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.PaymentMethod;
import com.schoolfinance.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_payment_number",
                        columnNames = "payment_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_payment_account",
                        columnList = "student_account_id"
                ),
                @Index(
                        name = "idx_payment_paid_at",
                        columnList = "paid_at"
                ),
                @Index(
                        name = "idx_payment_method",
                        columnList = "payment_method"
                ),
                @Index(
                        name = "idx_payment_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

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

    @Column(
            name = "payment_number",
            nullable = false,
            unique = true,
            length = 80
    )
    private String paymentNumber;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private PaymentStatus status = PaymentStatus.COMPLETED;

    @Column(
            name = "transaction_reference",
            length = 150
    )
    private String transactionReference;

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;

    @Column(
            name = "paid_at",
            nullable = false
    )
    private LocalDateTime paidAt;

    @Column(
            name = "received_by",
            nullable = false,
            length = 150
    )
    private String receivedBy;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}