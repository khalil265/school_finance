package com.schoolfinance.service;

import tools.jackson.databind.json.JsonMapper;
import com.schoolfinance.entity.audit.AuditLog;
import com.schoolfinance.repository.audit.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    private final JsonMapper jsonMapper;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            String action,
            String entityType,
            Object entityId,
            Object oldValue,
            Object newValue
    ) {

        AuditLog audit =
                AuditLog.builder()
                        .username(
                                currentUsername()
                        )
                        .action(action)
                        .entityType(entityType)
                        .entityId(
                                entityId == null
                                        ? null
                                        : entityId.toString()
                        )
                        .oldValue(
                                toJson(oldValue)
                        )
                        .newValue(
                                toJson(newValue)
                        )
                        .ipAddress(
                                currentIp()
                        )
                        .userAgent(
                                currentUserAgent()
                        )
                        .build();

        auditLogRepository.save(audit);
    }


    private String currentUsername() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                auth == null
                || auth.getName() == null
                || "anonymousUser".equals(auth.getName())
        ) {
            return "ANONYMOUS";
        }

        return auth.getName();
    }


    private String currentIp() {

        HttpServletRequest request =
                currentRequest();

        if (request == null) {
            return null;
        }

        String forwarded =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (
                forwarded != null
                && !forwarded.isBlank()
        ) {

            return forwarded
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }


    private String currentUserAgent() {

        HttpServletRequest request =
                currentRequest();

        return request == null
                ? null
                : request.getHeader(
                        "User-Agent"
                );
    }


    private HttpServletRequest currentRequest() {

        if (
                RequestContextHolder
                        .getRequestAttributes()
                        instanceof ServletRequestAttributes attributes
        ) {

            return attributes.getRequest();
        }

        return null;
    }


    private String toJson(
            Object value
    ) {

        if (value == null) {
            return null;
        }

        try {

            return jsonMapper
                    .writeValueAsString(
                            value
                    );

        }
        catch (Exception exception) {

            return String.valueOf(
                    value
            );
        }
    }
}