package com.schoolfinance.entity.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        schema = "school_finance",
        indexes = {
                @Index(
                        name = "idx_audit_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_audit_username",
                        columnList = "username"
                ),
                @Index(
                        name = "idx_audit_action",
                        columnList = "action"
                ),
                @Index(
                        name = "idx_audit_entity",
                        columnList = "entity_type,entity_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "username",
            nullable = false,
            length = 150
    )
    private String username;

    @Column(
            name = "action",
            nullable = false,
            length = 100
    )
    private String action;

    @Column(
            name = "entity_type",
            nullable = false,
            length = 150
    )
    private String entityType;

    @Column(
            name = "entity_id",
            length = 100
    )
    private String entityId;

    @Column(
            name = "old_value",
            columnDefinition = "TEXT"
    )
    private String oldValue;

    @Column(
            name = "new_value",
            columnDefinition = "TEXT"
    )
    private String newValue;

    @Column(
            name = "ip_address",
            length = 100
    )
    private String ipAddress;

    @Column(
            name = "user_agent",
            columnDefinition = "TEXT"
    )
    private String userAgent;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}