package com.schoolfinance.service;

import com.schoolfinance.dto.accounting.AccountingAccountResponse;
import com.schoolfinance.dto.accounting.CreateAccountingAccountRequest;
import com.schoolfinance.dto.accounting.UpdateAccountingAccountRequest;
import com.schoolfinance.entity.accounting.AccountingAccount;
import com.schoolfinance.entity.administration.Establishment;
import com.schoolfinance.enums.AccountingAccountType;
import com.schoolfinance.repository.accounting.AccountingAccountRepository;
import com.schoolfinance.repository.administration.EstablishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountingAccountService {

    private final AccountingAccountRepository accountRepository;

    private final EstablishmentRepository establishmentRepository;

    private final AuditService auditService;


    @Transactional
    public AccountingAccountResponse create(
            CreateAccountingAccountRequest request
    ) {

        Establishment establishment =
                establishmentRepository
                        .findById(request.establishmentId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Etablissement introuvable."
                                )
                        );


        String code =
                normalizeCode(
                        request.code()
                );


        if (
                accountRepository
                        .existsByEstablishmentIdAndCodeIgnoreCase(
                                establishment.getId(),
                                code
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un compte comptable avec ce code existe deja."
            );
        }


        AccountingAccount parent =
                resolveParent(
                        establishment.getId(),
                        request.parentId(),
                        request.accountType()
                );


        AccountingAccount account =
                AccountingAccount.builder()
                        .establishment(establishment)
                        .code(code)
                        .name(
                                request.name().trim()
                        )
                        .accountType(
                                request.accountType()
                        )
                        .parent(parent)
                        .description(
                                request.description()
                        )
                        .postingAllowed(
                                request.postingAllowed() == null
                                        ? true
                                        : request.postingAllowed()
                        )
                        .systemAccount(false)
                        .active(true)
                        .build();


        account =
                accountRepository.save(
                        account
                );


        AccountingAccountResponse response =
                toResponse(account);


        auditService.log(
                "ACCOUNTING_ACCOUNT_CREATED",
                "AccountingAccount",
                account.getId(),
                null,
                response
        );


        return response;
    }


    @Transactional(readOnly = true)
    public List<AccountingAccountResponse> list(
            UUID establishmentId,
            AccountingAccountType type,
            Boolean activeOnly
    ) {

        List<AccountingAccount> accounts;


        if (type != null) {

            accounts =
                    accountRepository
                            .findByEstablishmentIdAndAccountTypeAndActiveTrueOrderByCodeAsc(
                                    establishmentId,
                                    type
                            );

        }
        else if (Boolean.TRUE.equals(activeOnly)) {

            accounts =
                    accountRepository
                            .findByEstablishmentIdAndActiveTrueOrderByCodeAsc(
                                    establishmentId
                            );

        }
        else {

            accounts =
                    accountRepository
                            .findByEstablishmentIdOrderByCodeAsc(
                                    establishmentId
                            );
        }


        return accounts
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public AccountingAccountResponse get(
            UUID id
    ) {

        return toResponse(
                getEntity(id)
        );
    }


    @Transactional(readOnly = true)
    public AccountingAccountResponse getByCode(
            UUID establishmentId,
            String code
    ) {

        AccountingAccount account =
                accountRepository
                        .findByEstablishmentIdAndCodeIgnoreCase(
                                establishmentId,
                                normalizeCode(code)
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Compte comptable introuvable."
                                )
                        );


        return toResponse(account);
    }


    @Transactional
    public AccountingAccountResponse update(
            UUID id,
            UpdateAccountingAccountRequest request
    ) {

        AccountingAccount account =
                getEntity(id);


        if (
                Boolean.TRUE.equals(
                        account.getSystemAccount()
                )
                &&
                (
                        Boolean.FALSE.equals(request.active())
                        ||
                        Boolean.FALSE.equals(request.postingAllowed())
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un compte systeme ne peut pas etre desactive ou rendu non mouvementable par cette operation."
            );
        }


        AccountingAccountResponse oldValue =
                toResponse(account);


        account.setName(
                request.name().trim()
        );

        account.setDescription(
                request.description()
        );


        if (request.postingAllowed() != null) {

            account.setPostingAllowed(
                    request.postingAllowed()
            );
        }


        if (request.active() != null) {

            account.setActive(
                    request.active()
            );
        }


        account =
                accountRepository.save(
                        account
                );


        AccountingAccountResponse response =
                toResponse(account);


        auditService.log(
                "ACCOUNTING_ACCOUNT_UPDATED",
                "AccountingAccount",
                account.getId(),
                oldValue,
                response
        );


        return response;
    }


    @Transactional
    public AccountingAccountResponse deactivate(
            UUID id
    ) {

        AccountingAccount account =
                getEntity(id);


        if (
                Boolean.TRUE.equals(
                        account.getSystemAccount()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un compte systeme ne peut pas etre desactive."
            );
        }


        account.setActive(false);

        account =
                accountRepository.save(
                        account
                );


        auditService.log(
                "ACCOUNTING_ACCOUNT_DEACTIVATED",
                "AccountingAccount",
                account.getId(),
                "ACTIVE",
                "INACTIVE"
        );


        return toResponse(account);
    }


    private AccountingAccount resolveParent(
            UUID establishmentId,
            UUID parentId,
            AccountingAccountType accountType
    ) {

        if (parentId == null) {
            return null;
        }


        AccountingAccount parent =
                accountRepository
                        .findById(parentId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Compte parent introuvable."
                                )
                        );


        if (
                !parent.getEstablishment()
                        .getId()
                        .equals(establishmentId)
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le compte parent appartient a un autre etablissement."
            );
        }


        if (
                parent.getAccountType()
                        != accountType
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le compte parent doit avoir la meme nature comptable."
            );
        }


        if (
                !Boolean.TRUE.equals(
                        parent.getActive()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Le compte parent est inactif."
            );
        }


        return parent;
    }


    private AccountingAccount getEntity(
            UUID id
    ) {

        return accountRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Compte comptable introuvable."
                        )
                );
    }


    private String normalizeCode(
            String code
    ) {

        return code
                .trim()
                .toUpperCase();
    }


    private AccountingAccountResponse toResponse(
            AccountingAccount account
    ) {

        AccountingAccount parent =
                account.getParent();


        return new AccountingAccountResponse(
                account.getId(),
                account.getEstablishment().getId(),
                account.getCode(),
                account.getName(),
                account.getAccountType(),
                parent == null
                        ? null
                        : parent.getId(),
                parent == null
                        ? null
                        : parent.getCode(),
                parent == null
                        ? null
                        : parent.getName(),
                account.getDescription(),
                account.getPostingAllowed(),
                account.getSystemAccount(),
                account.getActive()
        );
    }
}