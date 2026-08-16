package com.schoolfinance.dto.administration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentResponse {

    private UUID id;
    private String code;
    private String name;
    private String legalName;
    private String address;
    private String city;
    private String country;
    private String phone;
    private String email;
    private String currency;
    private String logoUrl;
    private Boolean active;
}
