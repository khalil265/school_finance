package com.schoolfinance.config;

import com.schoolfinance.entity.security.Permission;
import com.schoolfinance.entity.security.Role;
import com.schoolfinance.entity.security.User;
import com.schoolfinance.repository.security.PermissionRepository;
import com.schoolfinance.repository.security.RoleRepository;
import com.schoolfinance.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Value("${app.bootstrap.admin.username}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.email}")
    private String adminEmail;


    @Override
    @Transactional
    public void run(String... args) {

        createPermissions();

        createRoles();

        configureRolePermissions();

        createSuperAdmin();
    }


    private void createPermissions() {

        List<PermissionDefinition> definitions =
                List.of(

                        new PermissionDefinition(
                                "ESTABLISHMENT_READ",
                                "Consulter les etablissements",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "ESTABLISHMENT_CREATE",
                                "Creer un etablissement",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "ESTABLISHMENT_UPDATE",
                                "Modifier un etablissement",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "USER_READ",
                                "Consulter les utilisateurs",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "USER_CREATE",
                                "Creer un utilisateur",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "USER_UPDATE",
                                "Modifier un utilisateur",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "ROLE_MANAGE",
                                "Gerer les roles",
                                "ADMINISTRATION"
                        ),

                        new PermissionDefinition(
                                "STUDENT_READ",
                                "Consulter les eleves",
                                "ACADEMIC"
                        ),

                        new PermissionDefinition(
                                "STUDENT_CREATE",
                                "Creer un eleve",
                                "ACADEMIC"
                        ),

                        new PermissionDefinition(
                                "STUDENT_UPDATE",
                                "Modifier un eleve",
                                "ACADEMIC"
                        ),

                        new PermissionDefinition(
                                "STUDENT_ENROLL",
                                "Inscrire un eleve",
                                "ACADEMIC"
                        ),

                        new PermissionDefinition(
                                "INVOICE_READ",
                                "Consulter les factures",
                                "BILLING"
                        ),

                        new PermissionDefinition(
                                "INVOICE_CREATE",
                                "Creer une facture",
                                "BILLING"
                        ),

                        new PermissionDefinition(
                                "INVOICE_CANCEL",
                                "Annuler une facture",
                                "BILLING"
                        ),

                        new PermissionDefinition(
                                "PAYMENT_READ",
                                "Consulter les paiements",
                                "PAYMENT"
                        ),

                        new PermissionDefinition(
                                "PAYMENT_CREATE",
                                "Enregistrer un paiement",
                                "PAYMENT"
                        ),

                        new PermissionDefinition(
                                "PAYMENT_VALIDATE",
                                "Valider un paiement",
                                "PAYMENT"
                        ),

                        new PermissionDefinition(
                                "PAYMENT_CANCEL",
                                "Annuler un paiement",
                                "PAYMENT"
                        ),

                        new PermissionDefinition(
                                "RECEIPT_PRINT",
                                "Imprimer un recu",
                                "PAYMENT"
                        ),

                        new PermissionDefinition(
                                "EXPENSE_READ",
                                "Consulter les depenses",
                                "EXPENSE"
                        ),

                        new PermissionDefinition(
                                "EXPENSE_CREATE",
                                "Creer une depense",
                                "EXPENSE"
                        ),

                        new PermissionDefinition(
                                "EXPENSE_VERIFY",
                                "Verifier une depense",
                                "EXPENSE"
                        ),

                        new PermissionDefinition(
                                "EXPENSE_APPROVE",
                                "Approuver une depense",
                                "EXPENSE"
                        ),

                        new PermissionDefinition(
                                "EXPENSE_PAY",
                                "Payer une depense",
                                "EXPENSE"
                        ),

                        new PermissionDefinition(
                                "BUDGET_READ",
                                "Consulter le budget",
                                "BUDGET"
                        ),

                        new PermissionDefinition(
                                "BUDGET_CREATE",
                                "Creer un budget",
                                "BUDGET"
                        ),

                        new PermissionDefinition(
                                "BUDGET_APPROVE",
                                "Approuver un budget",
                                "BUDGET"
                        ),

                        new PermissionDefinition(
                                "ACCOUNTING_READ",
                                "Consulter la comptabilite",
                                "ACCOUNTING"
                        ),

                        new PermissionDefinition(
                                "ACCOUNTING_ENTRY_CREATE",
                                "Creer une ecriture",
                                "ACCOUNTING"
                        ),

                        new PermissionDefinition(
                                "ACCOUNTING_ENTRY_POST",
                                "Valider une ecriture",
                                "ACCOUNTING"
                        ),

                        new PermissionDefinition(
                                "ACCOUNTING_PERIOD_CLOSE",
                                "Cloturer une periode",
                                "ACCOUNTING"
                        ),

                        new PermissionDefinition(
                                "CASH_READ",
                                "Consulter la caisse",
                                "TREASURY"
                        ),

                        new PermissionDefinition(
                                "CASH_OPEN",
                                "Ouvrir une caisse",
                                "TREASURY"
                        ),

                        new PermissionDefinition(
                                "CASH_CLOSE",
                                "Fermer une caisse",
                                "TREASURY"
                        ),

                        new PermissionDefinition(
                                "BANK_READ",
                                "Consulter les comptes bancaires",
                                "TREASURY"
                        ),

                        new PermissionDefinition(
                                "BANK_RECONCILE",
                                "Effectuer le rapprochement",
                                "TREASURY"
                        ),

                        new PermissionDefinition(
                                "PAYROLL_READ",
                                "Consulter la paie",
                                "PAYROLL"
                        ),

                        new PermissionDefinition(
                                "PAYROLL_CREATE",
                                "Creer la paie",
                                "PAYROLL"
                        ),

                        new PermissionDefinition(
                                "PAYROLL_VALIDATE",
                                "Valider la paie",
                                "PAYROLL"
                        ),

                        new PermissionDefinition(
                                "AUDIT_READ",
                                "Consulter les audits",
                                "AUDIT"
                        ),

                        new PermissionDefinition(
                                "REPORT_READ",
                                "Consulter les rapports",
                                "REPORTING"
                        ),

                        new PermissionDefinition(
                                "REPORT_EXPORT",
                                "Exporter les rapports",
                                "REPORTING"
                        ),

                        new PermissionDefinition(
                                "DASHBOARD_EXECUTIVE",
                                "Consulter le dashboard executif",
                                "DASHBOARD"
                        )
                );


        for (PermissionDefinition definition : definitions) {

            if (!permissionRepository.existsByCode(
                    definition.code()
            )) {

                Permission permission =
                        Permission.builder()
                                .code(definition.code())
                                .name(definition.name())
                                .module(definition.module())
                                .build();

                permissionRepository.save(
                        permission
                );
            }
        }
    }


    private void createRoles() {

        createRole(
                "SUPER_ADMIN",
                "Super Administrateur"
        );

        createRole(
                "CHIEF_ACCOUNTANT",
                "Chef Comptable"
        );

        createRole(
                "ACCOUNTANT",
                "Comptable"
        );

        createRole(
                "AUDITOR",
                "Controleur / Auditeur"
        );

        createRole(
                "DIRECTOR",
                "Proviseur / Directeur"
        );

        createRole(
                "CASHIER",
                "Caissier"
        );

        createRole(
                "DATA_ENTRY_AGENT",
                "Agent de Saisie"
        );
    }


    private void createRole(
            String code,
            String name
    ) {

        if (!roleRepository.existsByCode(code)) {

            Role role =
                    Role.builder()
                            .code(code)
                            .name(name)
                            .systemRole(true)
                            .active(true)
                            .build();

            roleRepository.save(role);
        }
    }


    private void configureRolePermissions() {

        assignAllPermissions(
                "SUPER_ADMIN"
        );


        assignPermissions(
                "DIRECTOR",
                Set.of(
                        "STUDENT_READ",
                        "INVOICE_READ",
                        "PAYMENT_READ",
                        "EXPENSE_READ",
                        "EXPENSE_APPROVE",
                        "BUDGET_READ",
                        "BUDGET_APPROVE",
                        "ACCOUNTING_READ",
                        "CASH_READ",
                        "BANK_READ",
                        "PAYROLL_READ",
                        "AUDIT_READ",
                        "REPORT_READ",
                        "REPORT_EXPORT",
                        "DASHBOARD_EXECUTIVE"
                )
        );


        assignPermissions(
                "CASHIER",
                Set.of(
                        "STUDENT_READ",
                        "INVOICE_READ",
                        "PAYMENT_READ",
                        "PAYMENT_CREATE",
                        "RECEIPT_PRINT",
                        "CASH_READ",
                        "CASH_OPEN",
                        "CASH_CLOSE"
                )
        );


        assignPermissions(
                "AUDITOR",
                Set.of(
                        "STUDENT_READ",
                        "INVOICE_READ",
                        "PAYMENT_READ",
                        "EXPENSE_READ",
                        "EXPENSE_VERIFY",
                        "BUDGET_READ",
                        "ACCOUNTING_READ",
                        "CASH_READ",
                        "BANK_READ",
                        "BANK_RECONCILE",
                        "PAYROLL_READ",
                        "AUDIT_READ",
                        "REPORT_READ",
                        "REPORT_EXPORT"
                )
        );


        assignPermissions(
                "ACCOUNTANT",
                Set.of(
                        "STUDENT_READ",
                        "INVOICE_READ",
                        "INVOICE_CREATE",
                        "PAYMENT_READ",
                        "EXPENSE_READ",
                        "EXPENSE_CREATE",
                        "EXPENSE_PAY",
                        "BUDGET_READ",
                        "ACCOUNTING_READ",
                        "ACCOUNTING_ENTRY_CREATE",
                        "ACCOUNTING_ENTRY_POST",
                        "CASH_READ",
                        "BANK_READ",
                        "PAYROLL_READ",
                        "PAYROLL_CREATE",
                        "REPORT_READ",
                        "REPORT_EXPORT"
                )
        );


        assignPermissions(
                "DATA_ENTRY_AGENT",
                Set.of(
                        "STUDENT_READ",
                        "STUDENT_CREATE",
                        "STUDENT_UPDATE",
                        "STUDENT_ENROLL"
                )
        );
    }


    private void assignAllPermissions(
            String roleCode
    ) {

        Role role =
                roleRepository
                        .findByCode(roleCode)
                        .orElseThrow();

        role.setPermissions(
                new HashSet<>(
                        permissionRepository.findAll()
                )
        );

        roleRepository.save(role);
    }


    private void assignPermissions(
            String roleCode,
            Set<String> permissionCodes
    ) {

        Role role =
                roleRepository
                        .findByCode(roleCode)
                        .orElseThrow();

        Set<Permission> permissions =
                new HashSet<>();

        for (String code : permissionCodes) {

            permissionRepository
                    .findByCode(code)
                    .ifPresent(
                            permissions::add
                    );
        }

        role.setPermissions(
                permissions
        );

        roleRepository.save(role);
    }


    private void createSuperAdmin() {

        if (userRepository.existsByUsername(
                adminUsername
        )) {
            return;
        }

        Role superAdminRole =
                roleRepository
                        .findByCode(
                                "SUPER_ADMIN"
                        )
                        .orElseThrow();


        User admin =
                User.builder()

                        .username(
                                adminUsername
                        )

                        .email(
                                adminEmail
                        )

                        .passwordHash(
                                passwordEncoder.encode(
                                        adminPassword
                                )
                        )

                        .firstName(
                                "Super"
                        )

                        .lastName(
                                "Administrateur"
                        )

                        .active(true)

                        .locked(false)

                        .failedLoginAttempts(0)

                        .passwordChangedAt(
                                LocalDateTime.now()
                        )

                        .roles(
                                new HashSet<>(
                                        Set.of(
                                                superAdminRole
                                        )
                                )
                        )

                        .build();


        userRepository.save(admin);


        System.out.println(
                "====================================================="
        );

        System.out.println(
                "SUPER ADMIN INITIAL CREE : " +
                        adminUsername
        );

        System.out.println(
                "====================================================="
        );
    }


    private record PermissionDefinition(
            String code,
            String name,
            String module
    ) {
    }
}