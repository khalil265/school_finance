package com.schoolfinance.repository.academic;

import com.schoolfinance.entity.academic.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LevelRepository extends JpaRepository<Level, UUID> {

    List<Level> findByEstablishmentIdAndActiveTrueOrderByDisplayOrderAsc(
            UUID establishmentId
    );

    boolean existsByEstablishmentIdAndCodeIgnoreCase(
            UUID establishmentId,
            String code
    );
}