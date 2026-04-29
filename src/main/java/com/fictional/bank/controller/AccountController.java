package com.fictional.bank.controller;


import java.util.Set;

import com.fictional.bank.request.CreateAccountRequest;
import com.fictional.bank.response.AccountResponse;
import com.fictional.bank.security.AuthUtils;
import com.fictional.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController
{
    private final AccountService accountService;
    private final AuthUtils authUtils;

    public AccountController(AccountService accountService, AuthUtils authUtils)
    {
        this.accountService = accountService;
        this.authUtils = authUtils;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse signUpUser(@Valid @RequestBody CreateAccountRequest request)
    {
        return this.accountService.createNewAccount(authUtils.getCurrentUser(), request);
    }

    @GetMapping
    public Set<AccountResponse> userAccounts()
    {
        return accountService.userAccountsDetails(authUtils.getCurrentUser());
    }


    @GetMapping("/{accountNumber}")
    public AccountResponse userAccount(@PathVariable String accountNumber)
    {
        return accountService.userAccountDetails(authUtils.getCurrentUser(), accountNumber);
    }
}
