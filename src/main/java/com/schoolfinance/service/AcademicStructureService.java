package com.schoolfinance.service;

import com.schoolfinance.dto.academic.*;
import com.schoolfinance.entity.academic.Level;
import com.schoolfinance.entity.academic.SchoolClass;
import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.repository.academic.LevelRepository;
import com.schoolfinance.repository.academic.SchoolClassRepository;
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
public class AcademicStructureService {

    private final LevelRepository levelRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AcademicYearRepository academicYearRepository;


    @Transactional
    public LevelResponse createLevel(
            LevelRequest request
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

        if (levelRepository
                .existsByEstablishmentIdAndCodeIgnoreCase(
                        establishment.getId(),
                        request.code()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce niveau existe deja."
            );
        }

        Level level =
                Level.builder()
                        .establishment(establishment)
                        .code(
                                request.code()
                                        .trim()
                                        .toUpperCase()
                        )
                        .name(
                                request.name().trim()
                        )
                        .description(
                                request.description()
                        )
                        .displayOrder(
                                request.displayOrder() == null
                                        ? 0
                                        : request.displayOrder()
                        )
                        .active(true)
                        .build();

        return toLevelResponse(
                levelRepository.save(level)
        );
    }


    @Transactional(readOnly = true)
    public List<LevelResponse> getLevels(
            UUID establishmentId
    ) {

        return levelRepository
                .findByEstablishmentIdAndActiveTrueOrderByDisplayOrderAsc(
                        establishmentId
                )
                .stream()
                .map(this::toLevelResponse)
                .toList();
    }


    @Transactional
    public SchoolClassResponse createClass(
            SchoolClassRequest request
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

        AcademicYear academicYear =
                academicYearRepository
                        .findById(
                                request.academicYearId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Annee academique introuvable."
                                )
                        );

        Level level =
                levelRepository
                        .findById(
                                request.levelId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Niveau introuvable."
                                )
                        );

        if (!academicYear.getEstablishment().getId()
                .equals(establishment.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'annee academique ne correspond pas a l'etablissement."
            );
        }

        if (!level.getEstablishment().getId()
                .equals(establishment.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le niveau ne correspond pas a l'etablissement."
            );
        }

        if (schoolClassRepository
                .existsByEstablishmentIdAndAcademicYearIdAndCodeIgnoreCase(
                        establishment.getId(),
                        academicYear.getId(),
                        request.code()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette classe existe deja pour cette annee academique."
            );
        }

        SchoolClass schoolClass =
                SchoolClass.builder()
                        .establishment(establishment)
                        .academicYear(academicYear)
                        .level(level)
                        .code(
                                request.code()
                                        .trim()
                                        .toUpperCase()
                        )
                        .name(
                                request.name().trim()
                        )
                        .capacity(
                                request.capacity()
                        )
                        .active(true)
                        .build();

        return toClassResponse(
                schoolClassRepository.save(
                        schoolClass
                )
        );
    }


    @Transactional(readOnly = true)
    public List<SchoolClassResponse> getClasses(
            UUID establishmentId,
            UUID academicYearId
    ) {

        return schoolClassRepository
                .findByEstablishmentIdAndAcademicYearIdAndActiveTrueOrderByNameAsc(
                        establishmentId,
                        academicYearId
                )
                .stream()
                .map(this::toClassResponse)
                .toList();
    }


    private LevelResponse toLevelResponse(
            Level level
    ) {

        return new LevelResponse(
                level.getId(),
                level.getEstablishment().getId(),
                level.getCode(),
                level.getName(),
                level.getDescription(),
                level.getDisplayOrder(),
                level.getActive()
        );
    }


    private SchoolClassResponse toClassResponse(
            SchoolClass schoolClass
    ) {

        return new SchoolClassResponse(
                schoolClass.getId(),
                schoolClass.getEstablishment().getId(),
                schoolClass.getAcademicYear().getId(),
                schoolClass.getAcademicYear().getLabel(),
                schoolClass.getLevel().getId(),
                schoolClass.getLevel().getName(),
                schoolClass.getCode(),
                schoolClass.getName(),
                schoolClass.getCapacity(),
                schoolClass.getActive()
        );
    }
}