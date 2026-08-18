package com.schoolfinance.controller;

import com.schoolfinance.dto.security.*;
import com.schoolfinance.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementService service;


    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public List<UserResponse> list(
            @RequestParam(required = false)
            UUID establishmentId
    ) {

        return service.list(
                establishmentId
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse get(
            @PathVariable
            UUID id
    ) {

        return service.get(id);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserResponse> create(
            @Valid
            @RequestBody
            UserCreateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.create(request)
                );
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse update(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UserUpdateRequest request
    ) {

        return service.update(
                id,
                request
        );
    }


    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse activate(
            @PathVariable
            UUID id
    ) {

        return service.activate(id);
    }


    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse deactivate(
            @PathVariable
            UUID id
    ) {

        return service.deactivate(id);
    }


    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse unlock(
            @PathVariable
            UUID id
    ) {

        return service.unlock(id);
    }


    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse resetPassword(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {

        return service.resetPassword(
                id,
                request
        );
    }
}