package com.schoolfinance.config;

import com.schoolfinance.entity.security.User;
import com.schoolfinance.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.admin-recovery.enabled",
        havingValue = "true"
)
public class AdminRecoveryRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) {

        String username =
                System.getProperty(
                        "admin.recovery.username",
                        "admin"
                );

        String password =
                System.getProperty(
                        "admin.recovery.password",
                        "Admin@2026!"
                );


        User admin =
                userRepository
                        .findAll()
                        .stream()
                        .filter(user ->
                                user.getUsername() != null
                                        &&
                                user.getUsername()
                                        .equalsIgnoreCase(username)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Utilisateur admin introuvable : "
                                                + username
                                )
                        );


        admin.setPasswordHash(
                passwordEncoder.encode(password)
        );

        admin.setActive(true);

        admin.setLocked(false);

        admin.setFailedLoginAttempts(0);


        userRepository.save(admin);


        System.out.println();
        System.out.println(
                "============================================"
        );

        System.out.println(
                "ADMIN SCHOOL FINANCE RECUPERE"
        );

        System.out.println(
                "Username : " + username
        );

        System.out.println(
                "Compte actif : true"
        );

        System.out.println(
                "Compte verrouille : false"
        );

        System.out.println(
                "Tentatives echouees : 0"
        );

        System.out.println(
                "============================================"
        );

        System.out.println();
    }
}