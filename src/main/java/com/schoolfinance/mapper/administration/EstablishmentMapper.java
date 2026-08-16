package com.schoolfinance.mapper.administration;

import com.schoolfinance.dto.administration.EstablishmentRequest;
import com.schoolfinance.dto.administration.EstablishmentResponse;
import com.schoolfinance.entity.administration.Establishment;
import org.springframework.stereotype.Component;

@Component
public class EstablishmentMapper {

    public EstablishmentResponse toResponse(Establishment entity) {
        if (entity == null) {
            return null;
        }

        return EstablishmentResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .legalName(entity.getLegalName())
                .address(entity.getAddress())
                .city(entity.getCity())
                .country(entity.getCountry())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .currency(entity.getCurrency())
                .logoUrl(entity.getLogoUrl())
                .active(entity.getActive())
                .build();
    }

    public Establishment toEntity(EstablishmentRequest request) {
        if (request == null) {
            return null;
        }

        return Establishment.builder()
                .code(request.getCode())
                .name(request.getName())
                .legalName(request.getLegalName())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry() != null ? request.getCountry() : "Senegal")
                .phone(request.getPhone())
                .email(request.getEmail())
                .currency(request.getCurrency() != null ? request.getCurrency() : "XOF")
                .logoUrl(request.getLogoUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public void updateEntity(Establishment entity, EstablishmentRequest request) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setLegalName(request.getLegalName());
        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());

        if (request.getCountry() != null) {
            entity.setCountry(request.getCountry());
        }

        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());

        if (request.getCurrency() != null) {
            entity.setCurrency(request.getCurrency());
        }

        entity.setLogoUrl(request.getLogoUrl());

        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
