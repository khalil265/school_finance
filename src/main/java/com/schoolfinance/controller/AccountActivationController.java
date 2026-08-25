package com.schoolfinance.controller;

import com.schoolfinance.dto.security.AccountActivationCheckResponse;
import com.schoolfinance.dto.security.ActivateAccountRequest;
import com.schoolfinance.service.AccountActivationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/account")
@RequiredArgsConstructor
public class AccountActivationController {

    private final AccountActivationService service;


    @GetMapping("/activate/{token}")
    public AccountActivationCheckResponse check(
            @PathVariable
            UUID token
    ) {

        return service.check(token);
    }


    @PostMapping("/activate")
    public ResponseEntity<Void> activate(
            @Valid
            @RequestBody
            ActivateAccountRequest request
    ) {

        service.activate(request);

        return ResponseEntity.ok().build();
    }
}