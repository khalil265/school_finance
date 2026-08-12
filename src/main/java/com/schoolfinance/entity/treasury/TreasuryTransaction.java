package com.schoolfinance.entity.treasury;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.entity.expense.ExpensePayment;
import com.schoolfinance.enums.PaymentMethod;
import com.schoolfinance.enums.TreasuryTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "treasury_transactions",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_treasury_transaction_number",
                        columnNames = "transaction_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_treasury_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_treasury_transaction_date",
                        columnList = "transaction_date"
                ),
                @Index(
                        name = "idx_treasury_type",
                        columnList = "transaction_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryTransaction extends BaseEntity {

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expense_payment_id",
            unique = true
    )
    private ExpensePayment expensePayment;

    @Column(
            name = "transaction_number",
            nullable = false,
            unique = true,
            length = 80
    )
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false,
            length = 30
    )
    private TreasuryTransactionType transactionType;

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
            name = "account_code",
            nullable = false,
            length = 80
    )
    private String accountCode;

    @Column(
            name = "external_reference",
            length = 150
    )
    private String externalReference;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "transaction_date",
            nullable = false
    )
    private LocalDateTime transactionDate;

    @Column(
            name = "created_by",
            nullable = false,
            length = 150
    )
    private String createdBy;
}