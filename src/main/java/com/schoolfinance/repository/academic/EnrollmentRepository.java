package com.schoolfinance.repository.academic;

import com.schoolfinance.entity.academic.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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
}