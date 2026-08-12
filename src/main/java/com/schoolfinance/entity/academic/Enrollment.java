package com.schoolfinance.entity.academic;

import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "enrollments",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_academic_year",
                        columnNames = {
                                "student_id",
                                "academic_year_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_enrollment_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_enrollment_class",
                        columnList = "school_class_id"
                ),
                @Index(
                        name = "idx_enrollment_year",
                        columnList = "academic_year_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

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
            name = "school_class_id",
            nullable = false
    )
    private SchoolClass schoolClass;

    @Column(
            name = "enrollment_date",
            nullable = false
    )
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;
}