package com.schoolfinance.repository.security;

import com.schoolfinance.entity.security.UserInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserInvitationRepository
        extends JpaRepository<UserInvitation, UUID> {

    Optional<UserInvitation> findByToken(UUID token);
}