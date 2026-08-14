package com.schoolfinance.entity.cash;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.CashSessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "cash_sessions",
        schema = "school_finance",
        indexes = {
                @Index(
                        name = "idx_cash_session_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_cash_session_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_cash_session_opened_at",
                        columnList = "opened_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashSession extends BaseEntity {

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

    @Column(
            name = "session_number",
            nullable = false,
            unique = true,
            length = 80
    )
    private String sessionNumber;

    @Column(
            name = "account_code",
            nullable = false,
            length = 30
    )
    private String accountCode;

    @Column(
            name = "opening_balance",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal openingBalance;

    @Column(
            name = "opened_at",
            nullable = false
    )
    private LocalDateTime openedAt;

    @Column(
            name = "opened_by",
            nullable = false,
            length = 150
    )
    private String openedBy;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private CashSessionStatus status =
            CashSessionStatus.OPEN;

    @Column(
            name = "total_inflows",
            precision = 19,
            scale = 2
    )
    private BigDecimal totalInflows;

    @Column(
            name = "total_outflows",
            precision = 19,
            scale = 2
    )
    private BigDecimal totalOutflows;

    @Column(
            name = "theoretical_balance",
            precision = 19,
            scale = 2
    )
    private BigDecimal theoreticalBalance;

    @Column(
            name = "physical_balance",
            precision = 19,
            scale = 2
    )
    private BigDecimal physicalBalance;

    @Column(
            name = "difference_amount",
            precision = 19,
            scale = 2
    )
    private BigDecimal differenceAmount;

    @Column(
            name = "closed_at"
    )
    private LocalDateTime closedAt;

    @Column(
            name = "closed_by",
            length = 150
    )
    private String closedBy;

    @Column(
            name = "closing_notes",
            columnDefinition = "TEXT"
    )
    private String closingNotes;
}