package com.schoolfinance.controller;

import com.schoolfinance.dto.academic.AcademicYearRequest;
import com.schoolfinance.dto.academic.AcademicYearResponse;
import com.schoolfinance.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService service;


    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<AcademicYearResponse> create(
            @Valid
            @RequestBody
            AcademicYearRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.create(
                                request
                        )
                );
    }


    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public List<AcademicYearResponse> findAll(
            @RequestParam
            UUID establishmentId
    ) {

        return service.findAll(
                establishmentId
        );
    }


    @PutMapping("/{id}/current")
    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    public AcademicYearResponse setCurrent(
            @PathVariable
            UUID id
    ) {

        return service.setCurrent(
                id
        );
    }
}