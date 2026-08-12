package com.schoolfinance.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class SecurityTestController {


    @GetMapping("/me")
    public Map<String, Object> me(
            Authentication authentication
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "username",
                authentication.getName()
        );

        response.put(
                "authorities",
                authentication
                        .getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .sorted()
                        .toList()
        );

        return response;
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Map<String, String> admin() {

        return Map.of(
                "message",
                "Acces SUPER_ADMIN autorise"
        );
    }


    @GetMapping("/audit")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public Map<String, String> audit() {

        return Map.of(
                "message",
                "Permission AUDIT_READ validee"
        );
    }
}