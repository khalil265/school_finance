package com.schoolfinance.entity.accounting;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.AccountingAccountType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "accounting_accounts",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_accounting_account_establishment_code",
                        columnNames = {
                                "establishment_id",
                                "code"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_accounting_account_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_accounting_account_code",
                        columnList = "code"
                ),
                @Index(
                        name = "idx_accounting_account_type",
                        columnList = "account_type"
                ),
                @Index(
                        name = "idx_accounting_account_parent",
                        columnList = "parent_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingAccount extends BaseEntity {

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
            name = "code",
            nullable = false,
            length = 30
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 200
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "account_type",
            nullable = false,
            length = 30
    )
    private AccountingAccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AccountingAccount parent;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    /**
     * Indique si le compte peut recevoir directement
     * des ecritures comptables.
     */
    @Column(
            name = "posting_allowed",
            nullable = false
    )
    @Builder.Default
    private Boolean postingAllowed = true;

    /**
     * Compte protege et utilise par le systeme.
     */
    @Column(
            name = "system_account",
            nullable = false
    )
    @Builder.Default
    private Boolean systemAccount = false;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}