package com.schoolfinance.controller;

import com.schoolfinance.dto.dashboard.DashboardResponse;
import com.schoolfinance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public DashboardResponse summary(
            @RequestParam
            UUID establishmentId
    ) {

        return dashboardService.summary(
                establishmentId
        );
    }
}