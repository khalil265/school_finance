package com.schoolfinance.service;

import com.schoolfinance.dto.academic.AcademicYearRequest;
import com.schoolfinance.dto.academic.AcademicYearResponse;
import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.repository.administration.AcademicYearRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    private final EstablishmentRepository establishmentRepository;


    @Transactional
    public AcademicYearResponse create(
            AcademicYearRequest request
    ) {

        Establishment establishment =
                establishmentRepository
                        .findById(
                                request.establishmentId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );


        if (
                academicYearRepository
                        .existsByEstablishmentAndCode(
                                establishment,
                                request.code().trim()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une annee academique avec ce code existe deja."
            );
        }


        if (
                !request.endDate()
                        .isAfter(
                                request.startDate()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La date de fin doit etre posterieure a la date de debut."
            );
        }


        boolean currentYear =
                Boolean.TRUE.equals(
                        request.currentYear()
                );


        if (currentYear) {

            academicYearRepository
                    .findByEstablishmentAndCurrentYearTrue(
                            establishment
                    )
                    .ifPresent(existing -> {

                        existing.setCurrentYear(false);

                        academicYearRepository.save(
                                existing
                        );
                    });
        }


        AcademicYear year =
                AcademicYear.builder()

                        .establishment(
                                establishment
                        )

                        .code(
                                request.code()
                                        .trim()
                        )

                        .label(
                                request.label()
                                        .trim()
                        )

                        .startDate(
                                request.startDate()
                        )

                        .endDate(
                                request.endDate()
                        )

                        .currentYear(
                                currentYear
                        )

                        .build();


        return toResponse(
                academicYearRepository.save(
                        year
                )
        );
    }


    @Transactional(readOnly = true)
    public List<AcademicYearResponse> findAll(
            UUID establishmentId
    ) {

        Establishment establishment =
                establishmentRepository
                        .findById(
                                establishmentId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );


        return academicYearRepository
                .findByEstablishmentOrderByStartDateDesc(
                        establishment
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional
    public AcademicYearResponse setCurrent(
            UUID id
    ) {

        AcademicYear year =
                academicYearRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Annee academique introuvable."
                                )
                        );


        Establishment establishment =
                year.getEstablishment();


        academicYearRepository
                .findByEstablishmentAndCurrentYearTrue(
                        establishment
                )
                .ifPresent(existing -> {

                    if (
                            !existing.getId()
                                    .equals(
                                            year.getId()
                                    )
                    ) {

                        existing.setCurrentYear(false);

                        academicYearRepository.save(
                                existing
                        );
                    }
                });


        year.setCurrentYear(true);


        return toResponse(
                academicYearRepository.save(
                        year
                )
        );
    }


    private AcademicYearResponse toResponse(
            AcademicYear year
    ) {

        return new AcademicYearResponse(

                year.getId(),

                year.getEstablishment()
                        .getId(),

                year.getCode(),

                year.getLabel(),

                year.getStartDate(),

                year.getEndDate(),

                year.getStatus(),

                year.getCurrentYear()
        );
    }
}