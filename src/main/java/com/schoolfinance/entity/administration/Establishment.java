package com.schoolfinance.entity.administration;

import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "establishments",
        schema = "school_finance"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Establishment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "code",
            nullable = false,
            unique = true,
            length = 50
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            name = "legal_name",
            length = 200
    )
    private String legalName;

    @Column(
            name = "address",
            columnDefinition = "TEXT"
    )
    private String address;

    @Column(
            name = "city",
            length = 100
    )
    private String city;

    @Column(
            name = "country",
            nullable = false,
            length = 100
    )
    @Builder.Default
    private String country = "Sénégal";

    @Column(
            name = "phone",
            length = 50
    )
    private String phone;

    @Column(
            name = "email",
            length = 150
    )
    private String email;

    @Column(
            name = "currency",
            nullable = false,
            length = 10
    )
    @Builder.Default
    private String currency = "XOF";

    @Column(
            name = "logo_url",
            columnDefinition = "TEXT"
    )
    private String logoUrl;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}
