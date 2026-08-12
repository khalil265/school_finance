package com.schoolfinance.config;

import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.enums.AccountingAccountType;
import com.schoolfinance.repository.accounting.AccountingAccountRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountingChartInitializer
        implements ApplicationRunner {

    private final EstablishmentRepository establishmentRepository;

    private final AccountingAccountRepository accountRepository;


    @Override
    @Transactional
    public void run(
            ApplicationArguments args
    ) {

        establishmentRepository
                .findAll()
                .forEach(
                        this::initializeEstablishment
                );
    }


    private void initializeEstablishment(
            Establishment establishment
    ) {

        AccountingAccount assets =
                ensureAccount(
                        establishment,
                        "5",
                        "Tresorerie et comptes financiers",
                        AccountingAccountType.ASSET,
                        null,
                        false
                );


        AccountingAccount banks =
                ensureAccount(
                        establishment,
                        "52",
                        "Banques",
                        AccountingAccountType.ASSET,
                        assets,
                        false
                );


        ensureAccount(
                establishment,
                "521100",
                "Banque principale",
                AccountingAccountType.ASSET,
                banks,
                true
        );


        AccountingAccount cash =
                ensureAccount(
                        establishment,
                        "57",
                        "Caisse",
                        AccountingAccountType.ASSET,
                        assets,
                        false
                );


        ensureAccount(
                establishment,
                "571000",
                "Caisse principale",
                AccountingAccountType.ASSET,
                cash,
                true
        );


        AccountingAccount expenses =
                ensureAccount(
                        establishment,
                        "6",
                        "Charges",
                        AccountingAccountType.EXPENSE,
                        null,
                        false
                );


        AccountingAccount purchases =
                ensureAccount(
                        establishment,
                        "60",
                        "Achats et fournitures",
                        AccountingAccountType.EXPENSE,
                        expenses,
                        false
                );


        ensureAccount(
                establishment,
                "604100",
                "Fournitures scolaires",
                AccountingAccountType.EXPENSE,
                purchases,
                true
        );


        AccountingAccount revenues =
                ensureAccount(
                        establishment,
                        "7",
                        "Produits",
                        AccountingAccountType.REVENUE,
                        null,
                        false
                );


        AccountingAccount schoolRevenue =
                ensureAccount(
                        establishment,
                        "70",
                        "Produits scolaires",
                        AccountingAccountType.REVENUE,
                        revenues,
                        false
                );


        ensureAccount(
                establishment,
                "706100",
                "Frais de scolarite",
                AccountingAccountType.REVENUE,
                schoolRevenue,
                true
        );


        ensureAccount(
                establishment,
                "706200",
                "Frais d'inscription",
                AccountingAccountType.REVENUE,
                schoolRevenue,
                true
        );


        ensureAccount(
                establishment,
                "706300",
                "Cantine scolaire",
                AccountingAccountType.REVENUE,
                schoolRevenue,
                true
        );


        ensureAccount(
                establishment,
                "706400",
                "Transport scolaire",
                AccountingAccountType.REVENUE,
                schoolRevenue,
                true
        );
    }


    private AccountingAccount ensureAccount(
            Establishment establishment,
            String code,
            String name,
            AccountingAccountType type,
            AccountingAccount parent,
            boolean postingAllowed
    ) {

        return accountRepository
                .findByEstablishmentIdAndCodeIgnoreCase(
                        establishment.getId(),
                        code
                )
                .orElseGet(() -> {

                    AccountingAccount account =
                            AccountingAccount.builder()
                                    .establishment(
                                            establishment
                                    )
                                    .code(code)
                                    .name(name)
                                    .accountType(type)
                                    .parent(parent)
                                    .postingAllowed(
                                            postingAllowed
                                    )
                                    .systemAccount(true)
                                    .active(true)
                                    .build();


                    return accountRepository.save(
                            account
                    );
                });
    }
}