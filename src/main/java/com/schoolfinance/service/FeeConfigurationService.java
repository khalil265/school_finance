package com.schoolfinance.service;

import com.schoolfinance.dto.finance.*;
import com.schoolfinance.entity.academic.Level;
import com.schoolfinance.entity.administration.AcademicYear;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.finance.FeeStructure;
import com.schoolfinance.entity.finance.FeeType;
import com.schoolfinance.repository.academic.LevelRepository;
import com.schoolfinance.repository.administration.AcademicYearRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.finance.FeeStructureRepository;
import com.schoolfinance.repository.finance.FeeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeConfigurationService {

    private final FeeTypeRepository feeTypeRepository;

    private final FeeStructureRepository feeStructureRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AcademicYearRepository academicYearRepository;

    private final LevelRepository levelRepository;


    @Transactional
    public FeeTypeResponse createFeeType(
            FeeTypeRequest request
    ) {

        Establishment establishment =
                establishmentRepository
                        .findById(request.establishmentId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );

        String code =
                request.code()
                        .trim()
                        .toUpperCase();

        if (feeTypeRepository
                .existsByEstablishmentIdAndCodeIgnoreCase(
                        establishment.getId(),
                        code
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un type de frais avec ce code existe deja."
            );
        }

        FeeType feeType =
                FeeType.builder()
                        .establishment(establishment)
                        .code(code)
                        .name(
                                request.name().trim()
                        )
                        .category(
                                request.category()
                        )
                        .frequency(
                                request.frequency()
                        )
                        .description(
                                request.description()
                        )
                        .mandatory(
                                request.mandatory() == null
                                        ? true
                                        : request.mandatory()
                        )
                        .active(true)
                        .build();

        return toFeeTypeResponse(
                feeTypeRepository.save(feeType)
        );
    }


    @Transactional(readOnly = true)
    public List<FeeTypeResponse> getFeeTypes(
            UUID establishmentId
    ) {

        return feeTypeRepository
                .findByEstablishmentIdAndActiveTrueOrderByNameAsc(
                        establishmentId
                )
                .stream()
                .map(this::toFeeTypeResponse)
                .toList();
    }


    @Transactional
    public FeeStructureResponse createFeeStructure(
            FeeStructureRequest request
    ) {

        Establishment establishment =
                establishmentRepository
                        .findById(request.establishmentId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );

        AcademicYear academicYear =
                academicYearRepository
                        .findById(request.academicYearId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Annee academique introuvable."
                                )
                        );

        Level level =
                levelRepository
                        .findById(request.levelId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Niveau introuvable."
                                )
                        );

        FeeType feeType =
                feeTypeRepository
                        .findById(request.feeTypeId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Type de frais introuvable."
                                )
                        );

        UUID establishmentId =
                establishment.getId();

        if (!academicYear.getEstablishment()
                .getId()
                .equals(establishmentId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'annee academique ne correspond pas a l'etablissement."
            );
        }

        if (!level.getEstablishment()
                .getId()
                .equals(establishmentId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le niveau ne correspond pas a l'etablissement."
            );
        }

        if (!feeType.getEstablishment()
                .getId()
                .equals(establishmentId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le type de frais ne correspond pas a l'etablissement."
            );
        }

        if (feeStructureRepository
                .existsByEstablishmentIdAndAcademicYearIdAndLevelIdAndFeeTypeId(
                        establishmentId,
                        academicYear.getId(),
                        level.getId(),
                        feeType.getId()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce tarif existe deja pour ce niveau, cette annee et ce type de frais."
            );
        }

        FeeStructure structure =
                FeeStructure.builder()
                        .establishment(establishment)
                        .academicYear(academicYear)
                        .level(level)
                        .feeType(feeType)
                        .amount(request.amount())
                        .installmentCount(
                                request.installmentCount() == null
                                        ? 1
                                        : request.installmentCount()
                        )
                        .firstDueDate(
                                request.firstDueDate()
                        )
                        .gracePeriodDays(
                                request.gracePeriodDays() == null
                                        ? 0
                                        : request.gracePeriodDays()
                        )
                        .active(true)
                        .build();

        return toFeeStructureResponse(
                feeStructureRepository.save(
                        structure
                )
        );
    }


    @Transactional(readOnly = true)
    public List<FeeStructureResponse> getFeeStructures(
            UUID establishmentId,
            UUID academicYearId,
            UUID levelId
    ) {

        return feeStructureRepository
                .findByEstablishmentIdAndAcademicYearIdAndLevelIdAndActiveTrueOrderByFeeTypeNameAsc(
                        establishmentId,
                        academicYearId,
                        levelId
                )
                .stream()
                .map(this::toFeeStructureResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<FeeStructureResponse> getFeeStructuresByLevel(
            UUID academicYearId,
            UUID levelId
    ) {

        return feeStructureRepository
                .findByAcademicYearIdAndLevelIdAndActiveTrue(
                        academicYearId,
                        levelId
                )
                .stream()
                .map(this::toFeeStructureResponse)
                .toList();
    }


    private FeeTypeResponse toFeeTypeResponse(
            FeeType feeType
    ) {

        return new FeeTypeResponse(
                feeType.getId(),
                feeType.getEstablishment().getId(),
                feeType.getEstablishment().getName(),
                feeType.getCode(),
                feeType.getName(),
                feeType.getCategory(),
                feeType.getFrequency(),
                feeType.getDescription(),
                feeType.getMandatory(),
                feeType.getActive()
        );
    }


    private FeeStructureResponse toFeeStructureResponse(
            FeeStructure structure
    ) {

        return new FeeStructureResponse(
                structure.getId(),
                structure.getEstablishment().getId(),
                structure.getAcademicYear().getId(),
                structure.getAcademicYear().getLabel(),
                structure.getLevel().getId(),
                structure.getLevel().getName(),
                structure.getFeeType().getId(),
                structure.getFeeType().getCode(),
                structure.getFeeType().getName(),
                structure.getFeeType().getCategory(),
                structure.getFeeType().getFrequency(),
                structure.getAmount(),
                structure.getInstallmentCount(),
                structure.getFirstDueDate(),
                structure.getGracePeriodDays(),
                structure.getActive()
        );
    }
}