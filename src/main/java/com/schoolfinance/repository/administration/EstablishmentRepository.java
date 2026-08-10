package com.schoolfinance.repository.administration;

import com.schoolfinance.entity.administration.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstablishmentRepository
        extends JpaRepository<Establishment, UUID> {

    Optional<Establishment> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByEmail(String email);
}
