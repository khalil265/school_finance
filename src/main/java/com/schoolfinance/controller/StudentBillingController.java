package com.schoolfinance.controller;

import com.schoolfinance.dto.finance.GenerateScheduleRequest;
import com.schoolfinance.dto.finance.ScheduleGenerationResponse;
import com.schoolfinance.dto.finance.StudentFinancialSummaryResponse;
import com.schoolfinance.service.StudentBillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class StudentBillingController {

    private final StudentBillingService service;


    @PostMapping("/schedules/generate")
    @PreAuthorize("hasAuthority('INVOICE_CREATE')")
    public ResponseEntity<ScheduleGenerationResponse> generate(
            @Valid
            @RequestBody
            GenerateScheduleRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.generateSchedule(
                                request
                        )
                );
    }


    @GetMapping("/students/{studentId}/summary")
    @PreAuthorize("hasAuthority('INVOICE_READ')")
    public StudentFinancialSummaryResponse summary(
            @PathVariable
            UUID studentId,

            @RequestParam
            UUID academicYearId
    ) {

        return service.getFinancialSummary(
                studentId,
                academicYearId
        );
    }


    @PostMapping("/students/{studentId}/refresh")
    @PreAuthorize("hasAuthority('INVOICE_READ')")
    public StudentFinancialSummaryResponse refresh(
            @PathVariable
            UUID studentId,

            @RequestParam
            UUID academicYearId
    ) {

        return service.refreshFinancialSummary(
                studentId,
                academicYearId
        );
    }
}