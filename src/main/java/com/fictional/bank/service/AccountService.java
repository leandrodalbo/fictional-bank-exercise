package com.fictional.bank.service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.response.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fictional.bank.model.Account;
import com.fictional.bank.model.Currency;
import com.fictional.bank.model.User;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateAccountRequest;
import com.fictional.bank.response.AccountResponse;

import static com.fictional.bank.utils.Utils.validateUser;

@Service
public class AccountService
{
    private static final Random RANDOM = new Random();

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository)
    {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public AccountResponse createNewAccount(String userMail, CreateAccountRequest request)
    {
        User savedUser = userRepository.findByEmail(userMail);

        Account account = Account.builder()
                .user(savedUser)
                .accountName(request.name())
                .accountNumber(generateAccountNumber())
                .accountType(request.accountType().getValue())
                .sortCode(generateSortCode())
                .balance(BigDecimal.ZERO)
                .currency(Currency.GBP.getValue())
                .build();

        Account saved = accountRepository.save(account);

        return new AccountResponse(
                saved.getAccountNumber(),
                saved.getSortCode(),
                saved.getAccountName(),
                saved.getAccountType(),
                saved.getBalance().toPlainString(),
                saved.getCurrency(),
                saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : "",
                saved.getUpdatedAt() != null ? saved.getUpdatedAt().toString() : ""
        );
    }

    public Set<AccountResponse> userAccountsDetails(String userMail)
    {
        User savedUser = userRepository.findByEmail(userMail);

        return accountRepository.findByUserId(savedUser.getId()).stream()
            .map(it -> new AccountResponse(
                it.getAccountNumber(),
                it.getSortCode(),
                it.getAccountName(),
                it.getAccountType(),
                it.getBalance() != null ? it.getBalance().toPlainString() : "0",
                it.getCurrency(),
                it.getCreatedAt() != null ? it.getCreatedAt().toString() : "",
                it.getUpdatedAt() != null ? it.getUpdatedAt().toString() : ""
            ))
            .collect(java.util.stream.Collectors.toSet());
    }

    public AccountResponse userAccountDetails(String userMail, String accountId)
    {
        User savedUser = userRepository.findByEmail(userMail);
        Account account = accountRepository.findByAccountNumber(accountId).orElseThrow(()-> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));

        validateUser(savedUser.getEmail(), account.getUser().getEmail());

        return new AccountResponse(
            account.getAccountNumber(),
            account.getSortCode(),
            account.getAccountName(),
            account.getAccountType(),
            account.getBalance() != null ? account.getBalance().toPlainString() : "0",
            account.getCurrency(),
            account.getCreatedAt() != null ? account.getCreatedAt().toString() : "",
            account.getUpdatedAt() != null ? account.getUpdatedAt().toString() : ""
        );
    }

    private String generateAccountNumber()
    {
        int number = 10000000 + RANDOM.nextInt(90000000);
        return String.valueOf(number);
    }

    private String generateSortCode()
    {
        int part1 = 10 + RANDOM.nextInt(90);
        int part2 = 10 + RANDOM.nextInt(90);
        int part3 = 10 + RANDOM.nextInt(90);
        return String.format("%02d-%02d-%02d", part1, part2, part3);
    }

}
