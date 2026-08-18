package com.schoolfinance.service;

import com.schoolfinance.dto.security.PermissionResponse;
import com.schoolfinance.entity.security.Permission;
import com.schoolfinance.repository.security.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionQueryService {

    private final PermissionRepository permissionRepository;


    @Transactional(readOnly = true)
    public List<PermissionResponse> list() {

        return permissionRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .sorted(
                        (a, b) ->
                                a.module().compareTo(b.module())
                )
                .toList();
    }


    private PermissionResponse toResponse(
            Permission permission
    ) {

        return new PermissionResponse(
                permission.getId(),
                permission.getCode(),
                permission.getName(),
                permission.getModule(),
                permission.getDescription()
        );
    }
}