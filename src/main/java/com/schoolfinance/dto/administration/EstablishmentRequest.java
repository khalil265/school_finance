package com.schoolfinance.dto.administration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentRequest {

    @NotBlank(message = "Le code est obligatoire.")
    @Size(max = 50, message = "Le code ne doit pas depasser 50 caracteres.")
    private String code;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 150, message = "Le nom ne doit pas depasser 150 caracteres.")
    private String name;

    @Size(max = 200, message = "La raison sociale ne doit pas depasser 200 caracteres.")
    private String legalName;

    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    @Size(max = 50)
    private String phone;

    @Email(message = "L'email doit etre valide.")
    @Size(max = 150)
    private String email;

    @Size(max = 10)
    private String currency;

    private String logoUrl;

    private Boolean active;
}
