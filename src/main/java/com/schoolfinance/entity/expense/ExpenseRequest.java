package com.schoolfinance.entity.expense;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.ExpenseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "expense_requests",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_expense_number",
                        columnNames = "expense_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_expense_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_expense_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_expense_supplier",
                        columnList = "supplier_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category_id")
    private ExpenseCategory category;

    @Column(
            name = "expense_number",
            nullable = false,
            unique = true,
            length = 80
    )
    private String expenseNumber;

    @Column(
            name = "subject",
            nullable = false,
            length = 250
    )
    private String subject;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    @Builder.Default
    private String currency = "XOF";

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ExpenseStatus status = ExpenseStatus.DRAFT;

    @Column(
            name = "requested_by",
            nullable = false,
            length = 150
    )
    private String requestedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(
            name = "verified_by",
            length = 150
    )
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(
            name = "approved_by",
            length = 150
    )
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(
            name = "rejected_by",
            length = 150
    )
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(
            name = "rejection_reason",
            columnDefinition = "TEXT"
    )
    private String rejectionReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(
            name = "payment_reference",
            length = 150
    )
    private String paymentReference;
}