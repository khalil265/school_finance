package com.schoolfinance.controller;

import com.schoolfinance.dto.finance.*;
import com.schoolfinance.service.FeeConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeeConfigurationController {

    private final FeeConfigurationService service;


    @PostMapping("/types")
    @PreAuthorize("hasAuthority('BUDGET_CREATE')")
    public ResponseEntity<FeeTypeResponse> createFeeType(
            @Valid
            @RequestBody
            FeeTypeRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createFeeType(request)
                );
    }


    @GetMapping("/types")
    @PreAuthorize("hasAuthority('BUDGET_READ')")
    public List<FeeTypeResponse> getFeeTypes(
            @RequestParam
            UUID establishmentId
    ) {

        return service.getFeeTypes(
                establishmentId
        );
    }


    @PostMapping("/structures")
    @PreAuthorize("hasAuthority('BUDGET_CREATE')")
    public ResponseEntity<FeeStructureResponse> createFeeStructure(
            @Valid
            @RequestBody
            FeeStructureRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createFeeStructure(request)
                );
    }


    @GetMapping("/structures")
    @PreAuthorize("hasAuthority('BUDGET_READ')")
    public List<FeeStructureResponse> getFeeStructures(
            @RequestParam
            UUID establishmentId,

            @RequestParam
            UUID academicYearId,

            @RequestParam
            UUID levelId
    ) {

        return service.getFeeStructures(
                establishmentId,
                academicYearId,
                levelId
        );
    }


    @GetMapping("/structures/level/{levelId}")
    @PreAuthorize("hasAuthority('BUDGET_READ')")
    public List<FeeStructureResponse> getByLevel(
            @PathVariable
            UUID levelId,

            @RequestParam
            UUID academicYearId
    ) {

        return service.getFeeStructuresByLevel(
                academicYearId,
                levelId
        );
    }
}