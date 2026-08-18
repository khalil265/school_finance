package com.schoolfinance.controller;

import com.schoolfinance.dto.security.RoleCreateRequest;
import com.schoolfinance.dto.security.RoleResponse;
import com.schoolfinance.dto.security.RoleUpdateRequest;
import com.schoolfinance.service.RoleManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleManagementService service;


    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public List<RoleResponse> list() {

        return service.list();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public RoleResponse get(
            @PathVariable
            UUID id
    ) {

        return service.get(id);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<RoleResponse> create(
            @Valid
            @RequestBody
            RoleCreateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.create(request)
                );
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public RoleResponse update(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            RoleUpdateRequest request
    ) {

        return service.update(
                id,
                request
        );
    }
}