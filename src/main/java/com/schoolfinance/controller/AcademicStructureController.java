package com.schoolfinance.controller;

import com.schoolfinance.dto.academic.*;
import com.schoolfinance.service.AcademicStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic")
@RequiredArgsConstructor
public class AcademicStructureController {

    private final AcademicStructureService service;


    @PostMapping("/levels")
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<LevelResponse> createLevel(
            @Valid
            @RequestBody
            LevelRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createLevel(request)
                );
    }


    @GetMapping("/levels")
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public List<LevelResponse> getLevels(
            @RequestParam
            UUID establishmentId
    ) {

        return service.getLevels(
                establishmentId
        );
    }


    @PostMapping("/classes")
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<SchoolClassResponse> createClass(
            @Valid
            @RequestBody
            SchoolClassRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createClass(request)
                );
    }


    @GetMapping("/classes")
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public List<SchoolClassResponse> getClasses(
            @RequestParam
            UUID establishmentId,

            @RequestParam
            UUID academicYearId
    ) {

        return service.getClasses(
                establishmentId,
                academicYearId
        );
    }
}