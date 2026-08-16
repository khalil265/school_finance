$utf8NoBom = New-Object System.Text.UTF8Encoding $false

function Write-FileNoBom($path, $content) {
    [System.IO.File]::WriteAllText($path, $content, $utf8NoBom)
    Write-Host "Ecrit : $path" -ForegroundColor Green
}

$enrollmentRepoPath = ".\src\main\java\com\schoolfinance\repository\academic\EnrollmentRepository.java"
$enrollmentRepoContent = @"
package com.schoolfinance.repository.academic;

import com.schoolfinance.entity.academic.Enrollment;
import com.schoolfinance.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, UUID> {

    boolean existsByStudentIdAndAcademicYearId(
            UUID studentId,
            UUID academicYearId
    );

    List<Enrollment>
    findByStudentIdOrderByEnrollmentDateDesc(
            UUID studentId
    );

    Optional<Enrollment>
    findFirstByStudentIdAndStatusOrderByEnrollmentDateDesc(
            UUID studentId,
            EnrollmentStatus status
    );
}
"@
Write-FileNoBom $enrollmentRepoPath $enrollmentRepoContent

$studentResponsePath = ".\src\main\java\com\schoolfinance\dto\student\StudentResponse.java"
$studentResponseContent = @"
package com.schoolfinance.dto.student;

import com.schoolfinance.enums.Gender;
import com.schoolfinance.enums.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudentResponse(

        UUID id,

        UUID establishmentId,

        String establishmentName,

        String registrationNumber,

        String firstName,

        String lastName,

        Gender gender,

        LocalDate dateOfBirth,

        String placeOfBirth,

        String nationality,

        String phone,

        String email,

        String address,

        String guardianName,

        String guardianPhone,

        String guardianEmail,

        StudentStatus status,

        UUID currentAcademicYearId,

        String currentAcademicYearLabel,

        UUID currentClassId,

        String currentClassName,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
"@
Write-FileNoBom $studentResponsePath $studentResponseContent

$studentServicePath = ".\src\main\java\com\schoolfinance\service\StudentService.java"
$studentServiceContent = @"
package com.schoolfinance.service;

import com.schoolfinance.dto.student.*;
import com.schoolfinance.entity.academic.Enrollment;
import com.schoolfinance.entity.academic.SchoolClass;
import com.schoolfinance.entity.academic.Student;
import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.enums.EnrollmentStatus;
import com.schoolfinance.enums.StudentStatus;
import com.schoolfinance.repository.academic.EnrollmentRepository;
import com.schoolfinance.repository.academic.SchoolClassRepository;
import com.schoolfinance.repository.academic.StudentRepository;
import com.schoolfinance.repository.administration.AcademicYearRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final AcademicYearRepository academicYearRepository;

    private final EstablishmentRepository establishmentRepository;


    @Transactional
    public StudentResponse create(
            StudentCreateRequest request
    ) {

        if (studentRepository.existsByRegistrationNumber(
                request.registrationNumber()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un eleve avec ce matricule existe deja."
            );
        }

        Establishment establishment =
                establishmentRepository
                        .findById(request.establishmentId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );

        Student student =
                Student.builder()
                        .establishment(establishment)
                        .registrationNumber(
                                request.registrationNumber().trim()
                        )
                        .firstName(
                                request.firstName().trim()
                        )
                        .lastName(
                                request.lastName().trim()
                        )
                        .gender(request.gender())
                        .dateOfBirth(request.dateOfBirth())
                        .placeOfBirth(request.placeOfBirth())
                        .nationality(
                                request.nationality() == null
                                        || request.nationality().isBlank()
                                        ? "Senegalaise"
                                        : request.nationality().trim()
                        )
                        .phone(request.phone())
                        .email(request.email())
                        .address(request.address())
                        .guardianName(request.guardianName())
                        .guardianPhone(request.guardianPhone())
                        .guardianEmail(request.guardianEmail())
                        .status(StudentStatus.ACTIVE)
                        .build();

        return toResponse(
                studentRepository.save(student)
        );
    }


    @Transactional(readOnly = true)
    public Page<StudentResponse> findAll(
            Pageable pageable
    ) {

        return studentRepository
                .findAll(pageable)
                .map(this::toResponse);
    }


    @Transactional(readOnly = true)
    public StudentResponse findById(
            UUID id
    ) {

        return toResponse(
                getStudent(id)
        );
    }


    @Transactional(readOnly = true)
    public Page<StudentResponse> search(
            String q,
            Pageable pageable
    ) {

        if (q == null || q.isBlank()) {
            return findAll(pageable);
        }

        return studentRepository
                .search(q.trim(), pageable)
                .map(this::toResponse);
    }


    @Transactional
    public StudentResponse update(
            UUID id,
            StudentUpdateRequest request
    ) {

        Student student = getStudent(id);

        student.setFirstName(
                request.firstName().trim()
        );

        student.setLastName(
                request.lastName().trim()
        );

        student.setGender(request.gender());

        student.setDateOfBirth(
                request.dateOfBirth()
        );

        student.setPlaceOfBirth(
                request.placeOfBirth()
        );

        student.setNationality(
                request.nationality()
        );

        student.setPhone(
                request.phone()
        );

        student.setEmail(
                request.email()
        );

        student.setAddress(
                request.address()
        );

        student.setGuardianName(
                request.guardianName()
        );

        student.setGuardianPhone(
                request.guardianPhone()
        );

        student.setGuardianEmail(
                request.guardianEmail()
        );

        student.setStatus(
                request.status()
        );

        return toResponse(
                studentRepository.save(student)
        );
    }


    @Transactional
    public EnrollmentResponse enroll(
            UUID studentId,
            EnrollmentRequest request
    ) {

        Student student =
                getStudent(studentId);

        AcademicYear academicYear =
                academicYearRepository
                        .findById(request.academicYearId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Annee academique introuvable."
                                )
                        );

        SchoolClass schoolClass =
                schoolClassRepository
                        .findById(request.schoolClassId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Classe introuvable."
                                )
                        );

        if (!student.getEstablishment().getId()
                .equals(academicYear.getEstablishment().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'annee academique ne correspond pas a l'etablissement de l'eleve."
            );
        }

        if (!student.getEstablishment().getId()
                .equals(schoolClass.getEstablishment().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La classe ne correspond pas a l'etablissement de l'eleve."
            );
        }

        if (!schoolClass.getAcademicYear().getId()
                .equals(academicYear.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La classe ne correspond pas a l'annee academique selectionnee."
            );
        }

        if (enrollmentRepository
                .existsByStudentIdAndAcademicYearId(
                        studentId,
                        academicYear.getId()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "L'eleve est deja inscrit pour cette annee academique."
            );
        }

        Enrollment enrollment =
                Enrollment.builder()
                        .student(student)
                        .academicYear(academicYear)
                        .schoolClass(schoolClass)
                        .enrollmentDate(
                                request.enrollmentDate() == null
                                        ? LocalDate.now()
                                        : request.enrollmentDate()
                        )
                        .status(
                                EnrollmentStatus.ACTIVE
                        )
                        .notes(
                                request.notes()
                        )
                        .build();

        return toEnrollmentResponse(
                enrollmentRepository.save(enrollment)
        );
    }


    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollments(
            UUID studentId
    ) {

        getStudent(studentId);

        return enrollmentRepository
                .findByStudentIdOrderByEnrollmentDateDesc(
                        studentId
                )
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }


    private Student getStudent(
            UUID id
    ) {

        return studentRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Eleve introuvable."
                        )
                );
    }


    private StudentResponse toResponse(
            Student student
    ) {

        Enrollment currentEnrollment =
                enrollmentRepository
                        .findFirstByStudentIdAndStatusOrderByEnrollmentDateDesc(
                                student.getId(),
                                EnrollmentStatus.ACTIVE
                        )
                        .orElse(null);

        return new StudentResponse(
                student.getId(),
                student.getEstablishment().getId(),
                student.getEstablishment().getName(),
                student.getRegistrationNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getDateOfBirth(),
                student.getPlaceOfBirth(),
                student.getNationality(),
                student.getPhone(),
                student.getEmail(),
                student.getAddress(),
                student.getGuardianName(),
                student.getGuardianPhone(),
                student.getGuardianEmail(),
                student.getStatus(),
                currentEnrollment != null
                        ? currentEnrollment.getAcademicYear().getId()
                        : null,
                currentEnrollment != null
                        ? currentEnrollment.getAcademicYear().getLabel()
                        : null,
                currentEnrollment != null
                        ? currentEnrollment.getSchoolClass().getId()
                        : null,
                currentEnrollment != null
                        ? currentEnrollment.getSchoolClass().getName()
                        : null,
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }


    private EnrollmentResponse toEnrollmentResponse(
            Enrollment enrollment
    ) {

        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getRegistrationNumber(),
                enrollment.getAcademicYear().getId(),
                enrollment.getAcademicYear().getLabel(),
                enrollment.getSchoolClass().getId(),
                enrollment.getSchoolClass().getName(),
                enrollment.getSchoolClass().getLevel().getName(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus(),
                enrollment.getNotes()
        );
    }
}
"@
Write-FileNoBom $studentServicePath $studentServiceContent

Write-Host ""
Write-Host "Termine. Lance maintenant : .\mvnw.cmd clean compile" -ForegroundColor Yellow