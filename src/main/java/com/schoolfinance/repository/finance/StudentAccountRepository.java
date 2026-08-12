package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.StudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentAccountRepository
        extends JpaRepository<StudentAccount, UUID> {

    Optional<StudentAccount>
    findByStudentIdAndAcademicYearId(
            UUID studentId,
            UUID academicYearId
    );

    boolean existsByStudentIdAndAcademicYearId(
            UUID studentId,
            UUID academicYearId
    );
}