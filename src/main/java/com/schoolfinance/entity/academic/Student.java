package com.schoolfinance.entity.academic;

import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.base.BaseEntity;
import com.schoolfinance.enums.Gender;
import com.schoolfinance.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "students",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_registration_number",
                        columnNames = "registration_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_student_establishment",
                        columnList = "establishment_id"
                ),
                @Index(
                        name = "idx_student_last_name",
                        columnList = "last_name"
                ),
                @Index(
                        name = "idx_student_registration",
                        columnList = "registration_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseEntity {

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
            name = "registration_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String registrationNumber;

    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "gender",
            nullable = false,
            length = 20
    )
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(
            name = "place_of_birth",
            length = 150
    )
    private String placeOfBirth;

    @Column(
            name = "nationality",
            length = 100
    )
    @Builder.Default
    private String nationality = "Senegalaise";

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
            name = "guardian_name",
            length = 200
    )
    private String guardianName;

    @Column(
            name = "guardian_phone",
            length = 50
    )
    private String guardianPhone;

    @Column(
            name = "guardian_email",
            length = 150
    )
    private String guardianEmail;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;
}