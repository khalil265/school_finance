package com.schoolfinance.entity.finance;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.FeeCategory;
import com.schoolfinance.enums.FeeFrequency;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "fee_types",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_fee_type_establishment_code",
                        columnNames = {
                                "establishment_id",
                                "code"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_fee_type_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_fee_type_category",
                        columnList = "category"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeType extends BaseEntity {

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
            length = 150
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            nullable = false,
            length = 30
    )
    private FeeCategory category;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "frequency",
            nullable = false,
            length = 30
    )
    private FeeFrequency frequency;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "mandatory",
            nullable = false
    )
    @Builder.Default
    private Boolean mandatory = true;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}