package com.schoolfinance.controller;

import com.schoolfinance.dto.administration.EstablishmentRequest;
import com.schoolfinance.dto.administration.EstablishmentResponse;
import com.schoolfinance.service.EstablishmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/establishments")
@RequiredArgsConstructor
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    @GetMapping
    public ResponseEntity<List<EstablishmentResponse>> findAll() {
        return ResponseEntity.ok(establishmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(establishmentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EstablishmentResponse> create(
            @Valid @RequestBody EstablishmentRequest request
    ) {
        EstablishmentResponse created = establishmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstablishmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody EstablishmentRequest request
    ) {
        return ResponseEntity.ok(establishmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        establishmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
