package com.schoolfinance.service;

import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.enums.AccountingAccountType;
import com.schoolfinance.repository.accounting.AccountingAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountingAccountValidationService {

    private final AccountingAccountRepository accountRepository;


    @Transactional(readOnly = true)
    public AccountingAccount requirePostingAccount(
            UUID establishmentId,
            String code,
            AccountingAccountType expectedType
    ) {

        if (
                code == null
                || code.isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le code du compte comptable est obligatoire."
            );
        }


        AccountingAccount account =
                accountRepository
                        .findByEstablishmentIdAndCodeIgnoreCase(
                                establishmentId,
                                code.trim()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Compte comptable inexistant : "
                                                + code
                                )
                        );


        if (
                !Boolean.TRUE.equals(
                        account.getActive()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le compte "
                            + account.getCode()
                            + " est inactif."
            );
        }


        if (
                !Boolean.TRUE.equals(
                        account.getPostingAllowed()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le compte "
                            + account.getCode()
                            + " est un compte collectif et ne peut pas recevoir directement d'ecriture."
            );
        }


        if (
                expectedType != null
                &&
                account.getAccountType()
                        != expectedType
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le compte "
                            + account.getCode()
                            + " doit etre de type "
                            + expectedType
                            + "."
            );
        }


        return account;
    }
}