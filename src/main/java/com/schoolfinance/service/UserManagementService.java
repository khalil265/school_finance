package com.schoolfinance.service;

import com.schoolfinance.dto.security.*;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.entity.security.Role;
import com.schoolfinance.entity.security.User;
import com.schoolfinance.entity.security.UserInvitation;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import com.schoolfinance.repository.security.RoleRepository;
import com.schoolfinance.repository.security.UserInvitationRepository;
import com.schoolfinance.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private static final long INVITATION_EXPIRATION_HOURS = 72;


    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final EstablishmentRepository establishmentRepository;

    private final UserInvitationRepository invitationRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;


    @Transactional(readOnly = true)
    public List<UserResponse> list(
            UUID establishmentId
    ) {

        return userRepository
                .findAll()
                .stream()
                .filter(u ->
                        establishmentId == null
                                || (u.getEstablishment() != null
                                && u.getEstablishment().getId().equals(establishmentId))
                )
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public UserResponse get(
            UUID id
    ) {

        return toResponse(
                getUserOrThrow(id)
        );
    }


    @Transactional
    public UserResponse create(
            UserCreateRequest request
    ) {

        String username =
                request.username().trim();

        String email =
                request.email().trim();

        if (userRepository.existsByUsername(username)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce nom d'utilisateur est deja utilise."
            );
        }

        if (userRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet email est deja utilise."
            );
        }

        Establishment establishment = null;

        if (request.establishmentId() != null) {

            establishment =
                    establishmentRepository
                            .findById(request.establishmentId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Etablissement introuvable."
                                    )
                            );
        }

        String temporaryPassword =
                UUID.randomUUID().toString();

        User user =
                User.builder()
                        .establishment(establishment)
                        .username(username)
                        .email(email)
                        .passwordHash(
                                passwordEncoder.encode(
                                        temporaryPassword
                                )
                        )
                        .firstName(request.firstName().trim())
                        .lastName(request.lastName().trim())
                        .phone(request.phone())
                        .active(false)
                        .locked(false)
                        .failedLoginAttempts(0)
                        .roles(
                                resolveRoles(
                                        request.roleIds()
                                )
                        )
                        .build();

        User saved =
                userRepository.save(user);

        UUID token =
                UUID.randomUUID();

        UserInvitation invitation =
                UserInvitation.builder()
                        .user(saved)
                        .token(token)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusHours(INVITATION_EXPIRATION_HOURS)
                        )
                        .used(false)
                        .build();

        invitationRepository.save(invitation);

        mailService.sendInvitation(
                saved.getEmail(),
                saved.getFirstName(),
                saved.getUsername(),
                token
        );

        return toResponse(saved);
    }


    @Transactional
    public UserResponse update(
            UUID id,
            UserUpdateRequest request
    ) {

        User user = getUserOrThrow(id);

        if (request.email() != null
                && !request.email().isBlank()
                && !request.email().equals(user.getEmail())
                && userRepository.existsByEmail(request.email())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet email est deja utilise."
            );
        }

        user.setEmail(
                blankToNull(request.email())
        );

        user.setFirstName(request.firstName().trim());

        user.setLastName(request.lastName().trim());

        user.setPhone(request.phone());

        if (request.roleIds() != null) {

            user.setRoles(
                    resolveRoles(
                            request.roleIds()
                    )
            );
        }

        return toResponse(
                userRepository.save(user)
        );
    }


    @Transactional
    public UserResponse activate(
            UUID id
    ) {

        User user = getUserOrThrow(id);

        user.setActive(true);

        return toResponse(
                userRepository.save(user)
        );
    }


    @Transactional
    public UserResponse deactivate(
            UUID id
    ) {

        User user = getUserOrThrow(id);

        user.setActive(false);

        return toResponse(
                userRepository.save(user)
        );
    }


    @Transactional
    public UserResponse unlock(
            UUID id
    ) {

        User user = getUserOrThrow(id);

        user.setLocked(false);

        user.setFailedLoginAttempts(0);

        return toResponse(
                userRepository.save(user)
        );
    }


    @Transactional
    public UserResponse resetPassword(
            UUID id,
            ResetPasswordRequest request
    ) {

        User user = getUserOrThrow(id);

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        user.setPasswordChangedAt(
                LocalDateTime.now()
        );

        return toResponse(
                userRepository.save(user)
        );
    }


    private Set<Role> resolveRoles(
            Set<UUID> roleIds
    ) {

        if (roleIds == null || roleIds.isEmpty()) {

            return new HashSet<>();
        }

        List<Role> found =
                roleRepository.findAllById(
                        roleIds
                );

        if (found.size() != roleIds.size()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Un ou plusieurs roles sont introuvables."
            );
        }

        return new HashSet<>(found);
    }


    private String blankToNull(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }


    private User getUserOrThrow(
            UUID id
    ) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Utilisateur introuvable."
                        )
                );
    }


    private UserResponse toResponse(
            User user
    ) {

        List<RoleSummaryResponse> roles =
                user.getRoles()
                        .stream()
                        .map(r ->
                                new RoleSummaryResponse(
                                        r.getId(),
                                        r.getCode(),
                                        r.getName()
                                )
                        )
                        .sorted(
                                (a, b) ->
                                        a.code().compareTo(b.code())
                        )
                        .toList();

        return new UserResponse(
                user.getId(),
                user.getEstablishment() != null
                        ? user.getEstablishment().getId()
                        : null,
                user.getEstablishment() != null
                        ? user.getEstablishment().getName()
                        : null,
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getActive(),
                user.getLocked(),
                user.getLastLoginAt(),
                roles
        );
    }
}