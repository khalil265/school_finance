package com.schoolfinance.repository.academic;

import com.schoolfinance.entity.academic.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SchoolClassRepository
        extends JpaRepository<SchoolClass, UUID> {

    List<SchoolClass>
    findByEstablishmentIdAndAcademicYearIdAndActiveTrueOrderByNameAsc(
            UUID establishmentId,
            UUID academicYearId
    );

    boolean existsByEstablishmentIdAndAcademicYearIdAndCodeIgnoreCase(
            UUID establishmentId,
            UUID academicYearId,
            String code
    );
}