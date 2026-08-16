package com.schoolfinance.service;

import com.schoolfinance.dto.administration.EstablishmentRequest;
import com.schoolfinance.dto.administration.EstablishmentResponse;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.mapper.administration.EstablishmentMapper;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentMapper establishmentMapper;

    public List<EstablishmentResponse> findAll() {
        return establishmentRepository.findAll()
                .stream()
                .map(establishmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EstablishmentResponse findById(UUID id) {
        Establishment establishment = getEstablishmentOrThrow(id);
        return establishmentMapper.toResponse(establishment);
    }

    @Transactional
    public EstablishmentResponse create(EstablishmentRequest request) {
        if (establishmentRepository.existsByCode(request.getCode())) {
            throw new IllegalStateException(
                    "Un etablissement avec le code '" + request.getCode() + "' existe deja."
            );
        }

        if (request.getEmail() != null && establishmentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException(
                    "Un etablissement avec l'email '" + request.getEmail() + "' existe deja."
            );
        }

        Establishment establishment = establishmentMapper.toEntity(request);
        Establishment saved = establishmentRepository.save(establishment);

        return establishmentMapper.toResponse(saved);
    }

    @Transactional
    public EstablishmentResponse update(UUID id, EstablishmentRequest request) {
        Establishment establishment = getEstablishmentOrThrow(id);

        if (!establishment.getCode().equals(request.getCode())
                && establishmentRepository.existsByCode(request.getCode())) {
            throw new IllegalStateException(
                    "Un etablissement avec le code '" + request.getCode() + "' existe deja."
            );
        }

        establishmentMapper.updateEntity(establishment, request);
        Establishment saved = establishmentRepository.save(establishment);

        return establishmentMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        Establishment establishment = getEstablishmentOrThrow(id);
        establishmentRepository.delete(establishment);
    }

    private Establishment getEstablishmentOrThrow(UUID id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Etablissement introuvable avec l'id : " + id
                ));
    }
}
