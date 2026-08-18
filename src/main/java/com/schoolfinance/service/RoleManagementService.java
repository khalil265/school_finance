package com.schoolfinance.service;

import com.schoolfinance.dto.security.PermissionResponse;
import com.schoolfinance.dto.security.RoleCreateRequest;
import com.schoolfinance.dto.security.RoleResponse;
import com.schoolfinance.dto.security.RoleUpdateRequest;
import com.schoolfinance.entity.security.Permission;
import com.schoolfinance.entity.security.Role;
import com.schoolfinance.repository.security.PermissionRepository;
import com.schoolfinance.repository.security.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleManagementService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;


    @Transactional(readOnly = true)
    public List<RoleResponse> list() {

        return roleRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public RoleResponse get(
            UUID id
    ) {

        return toResponse(
                getRoleOrThrow(id)
        );
    }


    @Transactional
    public RoleResponse create(
            RoleCreateRequest request
    ) {

        String code =
                request.code()
                        .trim()
                        .toUpperCase();

        if (roleRepository.existsByCode(code)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un role avec ce code existe deja."
            );
        }

        Role role =
                Role.builder()
                        .code(code)
                        .name(request.name().trim())
                        .description(request.description())
                        .systemRole(false)
                        .active(true)
                        .permissions(
                                resolvePermissions(
                                        request.permissionIds()
                                )
                        )
                        .build();

        return toResponse(
                roleRepository.save(role)
        );
    }


    @Transactional
    public RoleResponse update(
            UUID id,
            RoleUpdateRequest request
    ) {

        Role role = getRoleOrThrow(id);

        if (Boolean.TRUE.equals(role.getSystemRole())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce role systeme ne peut pas etre modifie."
            );
        }

        role.setName(request.name().trim());

        role.setDescription(request.description());

        if (request.permissionIds() != null) {

            role.setPermissions(
                    resolvePermissions(
                            request.permissionIds()
                    )
            );
        }

        if (request.active() != null) {

            role.setActive(request.active());
        }

        return toResponse(
                roleRepository.save(role)
        );
    }


    private Set<Permission> resolvePermissions(
            Set<UUID> permissionIds
    ) {

        if (permissionIds == null || permissionIds.isEmpty()) {

            return new HashSet<>();
        }

        List<Permission> found =
                permissionRepository.findAllById(
                        permissionIds
                );

        if (found.size() != permissionIds.size()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Une ou plusieurs permissions sont introuvables."
            );
        }

        return new HashSet<>(found);
    }


    private Role getRoleOrThrow(
            UUID id
    ) {

        return roleRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Role introuvable."
                        )
                );
    }


    private RoleResponse toResponse(
            Role role
    ) {

        List<PermissionResponse> permissions =
                role.getPermissions()
                        .stream()
                        .map(p ->
                                new PermissionResponse(
                                        p.getId(),
                                        p.getCode(),
                                        p.getName(),
                                        p.getModule(),
                                        p.getDescription()
                                )
                        )
                        .sorted(
                                (a, b) ->
                                        a.code().compareTo(b.code())
                        )
                        .toList();

        return new RoleResponse(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.getSystemRole(),
                role.getActive(),
                permissions
        );
    }
}