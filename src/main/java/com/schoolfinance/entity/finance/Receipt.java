package com.schoolfinance.entity.finance;

import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "receipts",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_receipt_number",
                        columnNames = "receipt_number"
                ),
                @UniqueConstraint(
                        name = "uq_receipt_payment",
                        columnNames = "payment_id"
                ),
                @UniqueConstraint(
                        name = "uq_receipt_verification_code",
                        columnNames = "verification_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "payment_id",
            nullable = false,
            unique = true
    )
    private Payment payment;

    @Column(
            name = "receipt_number",
            nullable = false,
            unique = true,
            length = 80
    )
    private String receiptNumber;

    @Column(
            name = "verification_code",
            nullable = false,
            unique = true
    )
    private UUID verificationCode;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "issued_at",
            nullable = false
    )
    private LocalDateTime issuedAt;

    @Column(
            name = "cancelled",
            nullable = false
    )
    @Builder.Default
    private Boolean cancelled = false;
}