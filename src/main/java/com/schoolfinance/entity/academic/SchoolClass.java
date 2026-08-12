package com.schoolfinance.entity.academic;

import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "school_classes",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_class_year_code",
                        columnNames = {
                                "establishment_id",
                                "academic_year_id",
                                "code"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_class_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_class_academic_year",
                        columnList = "academic_year_id"
                ),
                @Index(
                        name = "idx_class_level",
                        columnList = "level_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass extends BaseEntity {

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

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "academic_year_id",
            nullable = false
    )
    private AcademicYear academicYear;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "level_id",
            nullable = false
    )
    private Level level;

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

    @Column(name = "capacity")
    private Integer capacity;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}