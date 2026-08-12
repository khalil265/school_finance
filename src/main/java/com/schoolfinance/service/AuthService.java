package com.schoolfinance.service;

import com.schoolfinance.dto.auth.LoginRequest;
import com.schoolfinance.dto.auth.LoginResponse;
import com.schoolfinance.entity.security.Permission;
import com.schoolfinance.entity.security.Role;
import com.schoolfinance.entity.security.User;
import com.schoolfinance.repository.security.UserRepository;
import com.schoolfinance.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;


    @Transactional
    public LoginResponse login(
            LoginRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        User user = userRepository
                .findWithRolesByUsername(
                        request.username()
                )
                .orElseThrow();

        user.setLastLoginAt(
                LocalDateTime.now()
        );

        user.setFailedLoginAttempts(0);

        userRepository.save(user);

        String token =
                jwtService.generateAccessToken(
                        authentication
                );

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(Role::getCode)
                        .sorted()
                        .toList();

        List<String> permissions =
                user.getRoles()
                        .stream()
                        .flatMap(
                                role ->
                                        role.getPermissions()
                                                .stream()
                        )
                        .map(Permission::getCode)
                        .distinct()
                        .sorted()
                        .toList();

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds(),
                user.getUsername(),
                roles,
                permissions
        );
    }
}