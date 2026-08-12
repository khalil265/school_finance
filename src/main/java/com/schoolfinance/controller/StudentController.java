package com.schoolfinance.controller;

import com.schoolfinance.dto.student.*;
import com.schoolfinance.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;


    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT_CREATE')")
    public ResponseEntity<StudentResponse> create(
            @Valid
            @RequestBody
            StudentCreateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        studentService.create(request)
                );
    }


    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public Page<StudentResponse> findAll(
            Pageable pageable
    ) {

        return studentService.findAll(
                pageable
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public StudentResponse findById(
            @PathVariable
            UUID id
    ) {

        return studentService.findById(id);
    }


    @GetMapping("/search")
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public Page<StudentResponse> search(
            @RequestParam(required = false)
            String q,

            Pageable pageable
    ) {

        return studentService.search(
                q,
                pageable
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    public StudentResponse update(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            StudentUpdateRequest request
    ) {

        return studentService.update(
                id,
                request
        );
    }


    @PostMapping("/{id}/enrollments")
    @PreAuthorize("hasAuthority('STUDENT_ENROLL')")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            EnrollmentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        studentService.enroll(
                                id,
                                request
                        )
                );
    }


    @GetMapping("/{id}/enrollments")
    @PreAuthorize("hasAuthority('STUDENT_READ')")
    public List<EnrollmentResponse> getEnrollments(
            @PathVariable
            UUID id
    ) {

        return studentService
                .getEnrollments(id);
    }
}