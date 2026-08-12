package com.schoolfinance.entity.expense;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "suppliers",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_supplier_establishment_code",
                        columnNames = {
                                "establishment_id",
                                "code"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_supplier_establishment",
                        columnList = "establishment_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

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
            length = 50
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 200
    )
    private String name;

    @Column(
            name = "tax_identifier",
            length = 100
    )
    private String taxIdentifier;

    @Column(
            name = "phone",
            length = 50
    )
    private String phone;

    @Column(
            name = "email",
            length = 150
    )
    private String email;

    @Column(
            name = "address",
            columnDefinition = "TEXT"
    )
    private String address;

    @Column(
            name = "bank_name",
            length = 150
    )
    private String bankName;

    @Column(
            name = "bank_account",
            length = 150
    )
    private String bankAccount;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}