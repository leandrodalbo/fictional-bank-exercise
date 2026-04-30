package com.fictional.bank.controller;


import com.fictional.bank.request.CreateBankAccountRequest;
import com.fictional.bank.request.CreateTransactionRequest;
import com.fictional.bank.request.UpdateBankAccountRequest;
import com.fictional.bank.response.BankAccountResponse;
import com.fictional.bank.response.ListBankAccountsResponse;
import com.fictional.bank.response.ListTransactionsResponse;
import com.fictional.bank.response.TransactionResponse;
import com.fictional.bank.security.AuthUtils;
import com.fictional.bank.service.AccountService;
import com.fictional.bank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final TransactionService transactionService;

    private final AuthUtils authUtils;

    public AccountController(AccountService accountService, TransactionService transactionService, AuthUtils authUtils)
    {
        this.accountService = accountService;
        this.transactionService = transactionService;
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

    @DeleteMapping("/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserAccount(@PathVariable String accountNumber)
    {
        accountService.deleteUserAccount(authUtils.getCurrentUser(), accountNumber);
    }

    @PostMapping("/{accountNumber}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @PathVariable String accountNumber,
            @Valid @RequestBody CreateTransactionRequest request
    )
    {
        return transactionService.handleTransaction(
                authUtils.getCurrentUser(),
                accountNumber,
                request
        );
    }

    @GetMapping("/{accountNumber}/transactions")
    public ListTransactionsResponse listTransactions(@PathVariable String accountNumber)
    {
        return transactionService.userTransactions(
                authUtils.getCurrentUser(),
                accountNumber
        );
    }

    @GetMapping("/{accountNumber}/transactions/{transactionId}")
    public TransactionResponse getTransaction(
            @PathVariable String accountNumber,
            @PathVariable String transactionId
    )
    {
        return transactionService.userTransaction(
                authUtils.getCurrentUser(),
                accountNumber,
                extractTransactionId(transactionId)
        );
    }

    private Long extractTransactionId(String transactionId)
    {
        return Long.valueOf(transactionId.split("tan-")[1]);
    }
}
