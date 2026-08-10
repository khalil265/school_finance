package com.schoolfinance.repository.administration;

import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcademicYearRepository
        extends JpaRepository<AcademicYear, UUID> {

    List<AcademicYear> findByEstablishmentOrderByStartDateDesc(
            Establishment establishment
    );

    Optional<AcademicYear>
    findByEstablishmentAndCurrentYearTrue(
            Establishment establishment
    );

    boolean existsByEstablishmentAndCode(
            Establishment establishment,
            String code
    );
}
