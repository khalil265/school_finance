package com.schoolfinance.repository.academic;

import com.schoolfinance.entity.academic.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository
        extends JpaRepository<Student, UUID> {

    Optional<Student> findByRegistrationNumber(
            String registrationNumber
    );

    boolean existsByRegistrationNumber(
            String registrationNumber
    );

    Page<Student> findByEstablishmentId(
            UUID establishmentId,
            Pageable pageable
    );

    @Query("""
            select s
            from Student s
            where
                lower(s.registrationNumber) like lower(concat('%', :q, '%'))
                or lower(s.firstName) like lower(concat('%', :q, '%'))
                or lower(s.lastName) like lower(concat('%', :q, '%'))
                or lower(concat(s.firstName, ' ', s.lastName))
                    like lower(concat('%', :q, '%'))
            """)
    Page<Student> search(
            @Param("q") String q,
            Pageable pageable
    );
}