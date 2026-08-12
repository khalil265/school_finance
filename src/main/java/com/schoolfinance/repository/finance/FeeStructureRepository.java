package com.schoolfinance.repository.finance;

import com.schoolfinance.entity.finance.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeeStructureRepository
        extends JpaRepository<FeeStructure, UUID> {

    boolean existsByEstablishmentIdAndAcademicYearIdAndLevelIdAndFeeTypeId(
            UUID establishmentId,
            UUID academicYearId,
            UUID levelId,
            UUID feeTypeId
    );

    List<FeeStructure>
    findByEstablishmentIdAndAcademicYearIdAndLevelIdAndActiveTrueOrderByFeeTypeNameAsc(
            UUID establishmentId,
            UUID academicYearId,
            UUID levelId
    );

    List<FeeStructure>
    findByAcademicYearIdAndLevelIdAndActiveTrue(
            UUID academicYearId,
            UUID levelId
    );
}