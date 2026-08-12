package com.schoolfinance.controller;

import com.schoolfinance.dto.audit.AuditLogResponse;
import com.schoolfinance.entity.audit.AuditLog;
import com.schoolfinance.repository.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;


    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public Page<AuditLogResponse> logs(
            Pageable pageable
    ) {

        return auditLogRepository
                .findAll(pageable)
                .map(
                        this::toResponse
                );
    }


    private AuditLogResponse toResponse(
            AuditLog log
    ) {

        return new AuditLogResponse(
                log.getId(),
                log.getUsername(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getOldValue(),
                log.getNewValue(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getCreatedAt()
        );
    }
}