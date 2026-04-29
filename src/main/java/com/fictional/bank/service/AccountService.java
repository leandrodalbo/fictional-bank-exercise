package com.fictional.bank.service;

import java.math.BigDecimal;
import java.util.Random;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.request.CreateBankAccountRequest;

import com.fictional.bank.request.UpdateBankAccountRequest;
import com.fictional.bank.response.BankAccountResponse;
import com.fictional.bank.response.ListBankAccountsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fictional.bank.entity.Account;
import com.fictional.bank.entity.User;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.UserRepository;

import static com.fictional.bank.utils.Utils.GBP_CURRENCY;
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
    public BankAccountResponse createNewAccount(String userMail, CreateBankAccountRequest request)
    {
        User savedUser = userRepository.findByEmail(userMail);

        Account account = Account.builder()
                .user(savedUser)
                .accountName(request.name())
                .accountNumber(generateAccountNumber())
                .accountType(request.accountType())
                .sortCode(generateSortCode())
                .balance(BigDecimal.ZERO)
                .currency(GBP_CURRENCY)
                .build();

        Account saved = accountRepository.save(account);

        return new BankAccountResponse(
                saved.getAccountNumber(),
                saved.getSortCode(),
                saved.getAccountName(),
                saved.getAccountType(),
                saved.getBalance(),
                saved.getCurrency(),
                saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : "",
                saved.getUpdatedAt() != null ? saved.getUpdatedAt().toString() : ""
        );
    }

    public ListBankAccountsResponse userAccountsDetails(String userMail)
    {
        User savedUser = userRepository.findByEmail(userMail);

        return new ListBankAccountsResponse(accountRepository.findByUserId(savedUser.getId()).stream()
                                                    .map(it -> new BankAccountResponse(
                                                            it.getAccountNumber(),
                                                            it.getSortCode(),
                                                            it.getAccountName(),
                                                            it.getAccountType(),
                                                            it.getBalance(),
                                                            it.getCurrency(),
                                                            it.getCreatedAt() != null ? it.getCreatedAt().toString() : "",
                                                            it.getUpdatedAt() != null ? it.getUpdatedAt().toString() : ""
                                                    )).toList());

    }

    public BankAccountResponse userAccountDetails(String userMail, String accountNumber)
    {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));

        validateUser(userMail, account.getUser().getEmail());

        return new BankAccountResponse(
                account.getAccountNumber(),
                account.getSortCode(),
                account.getAccountName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedAt() != null ? account.getCreatedAt().toString() : "",
                account.getUpdatedAt() != null ? account.getUpdatedAt().toString() : ""
        );
    }

    @Transactional
    public BankAccountResponse updateUserAccountDetails(String userMail, String accountNumber, UpdateBankAccountRequest request)
    {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));

        validateUser(userMail, account.getUser().getEmail());

        account.setAccountType(request.accountType());
        account.setAccountName(request.name());

        Account updated = accountRepository.save(account);

        return new BankAccountResponse(
                updated.getAccountNumber(),
                updated.getSortCode(),
                updated.getAccountName(),
                updated.getAccountType(),
                updated.getBalance(),
                updated.getCurrency(),
                updated.getCreatedAt() != null ? updated.getCreatedAt().toString() : "",
                updated.getUpdatedAt() != null ? updated.getUpdatedAt().toString() : ""
        );
    }

    @Transactional
    public void deleteUserAccount(String userMail, String accountNumber)
    {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));

        validateUser(account.getUser().getEmail(), userMail);

        accountRepository.deleteById(account.getId());
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
