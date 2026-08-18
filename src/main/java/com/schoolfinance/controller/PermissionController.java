package com.schoolfinance.controller;

import com.schoolfinance.dto.security.PermissionResponse;
import com.schoolfinance.service.PermissionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionQueryService service;


    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public List<PermissionResponse> list() {

        return service.list();
    }
}