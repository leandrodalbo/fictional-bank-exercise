package com.fictional.bank.controller;


import com.fictional.bank.request.CreateBankAccountRequest;
import com.fictional.bank.request.UpdateBankAccountRequest;
import com.fictional.bank.response.BankAccountResponse;
import com.fictional.bank.response.ListBankAccountsResponse;
import com.fictional.bank.security.AuthUtils;
import com.fictional.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public BankAccountResponse signUpUser(@Valid @RequestBody CreateBankAccountRequest request)
    {
        return this.accountService.createNewAccount(authUtils.getCurrentUser(), request);
    }

    @GetMapping
    public ListBankAccountsResponse userAccounts()
    {
        return accountService.userAccountsDetails(authUtils.getCurrentUser());
    }


    @GetMapping("/{accountNumber}")
    public BankAccountResponse userAccount(@PathVariable String accountNumber)
    {
        return accountService.userAccountDetails(authUtils.getCurrentUser(), accountNumber);
    }

    @PatchMapping("/{accountNumber}")
    public BankAccountResponse patchAccount(@PathVariable String accountNumber, @RequestBody UpdateBankAccountRequest request)
    {
        return accountService.updateUserAccountDetails(authUtils.getCurrentUser(), accountNumber, request);
    }
}
