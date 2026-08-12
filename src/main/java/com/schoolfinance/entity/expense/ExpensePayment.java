package com.schoolfinance.entity.expense;

import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "expense_payments",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_expense_payment_expense",
                        columnNames = "expense_request_id"
                ),
                @UniqueConstraint(
                        name = "uq_expense_payment_number",
                        columnNames = "payment_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_expense_payment_expense",
                        columnList = "expense_request_id"
                ),
                @Index(
                        name = "idx_expense_payment_paid_at",
                        columnList = "paid_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpensePayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @Column(
            name = "payment_reference",
            length = 150
    )
    private String paymentReference;

    @Column(
            name = "paid_at",
            nullable = false
    )
    private LocalDateTime paidAt;

    @Column(
            name = "paid_by",
            nullable = false,
            length = 150
    )
    private String paidBy;

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;
}