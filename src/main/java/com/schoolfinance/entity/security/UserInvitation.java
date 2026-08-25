package com.schoolfinance.entity.security;

import com.schoolfinance.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_invitations",
        schema = "school_finance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_invitation_token",
                        columnNames = "token"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInvitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "token",
            nullable = false,
            unique = true
    )
    private UUID token;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "used",
            nullable = false
    )
    @Builder.Default
    private Boolean used = false;
}