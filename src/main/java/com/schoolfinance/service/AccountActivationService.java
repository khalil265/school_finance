package com.schoolfinance.service;

import com.schoolfinance.dto.security.AccountActivationCheckResponse;
import com.schoolfinance.dto.security.ActivateAccountRequest;
import com.schoolfinance.entity.security.User;
import com.schoolfinance.entity.security.UserInvitation;
import com.schoolfinance.repository.security.UserInvitationRepository;
import com.schoolfinance.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountActivationService {

    private final UserInvitationRepository invitationRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public AccountActivationCheckResponse check(
            UUID token
    ) {

        UserInvitation invitation =
                invitationRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Lien d'activation invalide."
                                )
                        );

        boolean expired =
                invitation.getExpiresAt()
                        .isBefore(LocalDateTime.now());

        boolean valid =
                !Boolean.TRUE.equals(invitation.getUsed())
                && !expired;

        return new AccountActivationCheckResponse(
                valid,
                invitation.getUser().getUsername(),
                invitation.getUser().getFirstName(),
                expired
        );
    }


    @Transactional
    public void activate(
            ActivateAccountRequest request
    ) {

        UserInvitation invitation =
                invitationRepository
                        .findByToken(request.token())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Lien d'activation invalide."
                                )
                        );

        if (Boolean.TRUE.equals(invitation.getUsed())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce lien a deja ete utilise."
            );
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ce lien d'activation a expire."
            );
        }

        User user = invitation.getUser();

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setActive(true);

        user.setPasswordChangedAt(
                LocalDateTime.now()
        );

        user.setFailedLoginAttempts(0);

        userRepository.save(user);

        invitation.setUsed(true);

        invitationRepository.save(invitation);
    }
}