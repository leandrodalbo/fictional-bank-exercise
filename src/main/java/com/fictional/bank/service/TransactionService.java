package com.fictional.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fictional.bank.entity.Account;
import com.fictional.bank.entity.Transaction;
import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiException;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.TransactionRepository;
import com.fictional.bank.request.CreateTransactionRequest;
import com.fictional.bank.response.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fictional.bank.utils.Utils.validateUser;

@Service
public class TransactionService
{
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository)
    {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionResponse> userTransactions(String userMail, String accountNumber)
    {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));

        validateUser(account.getUser().getEmail(), userMail);

        List<Transaction> accountTransactions = transactionRepository.findByAccountId(account.getId());

        return accountTransactions.stream().map(
                it -> new TransactionResponse(
                        it.getId().toString(),
                        it.getAmount(),
                        it.getCurrency(),
                        it.getType(),
                        it.getReference(),
                        it.getAccount().getUser().getId().toString(),
                        it.getCreatedAt().toString()
                )
        ).toList();

    }

    public TransactionResponse userTransaction(String userMail, String accountNumber, Long transactionId)
    {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));

        validateUser(account.getUser().getEmail(), userMail);

        Transaction it = transactionRepository.findTransaction(account.getId(), transactionId)
                .orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.TRANSACTION_NOT_FOUND));

        return new TransactionResponse(
                it.getId().toString(),
                it.getAmount(),
                it.getCurrency(),
                it.getType(),
                it.getReference(),
                it.getAccount().getUser().getId().toString(),
                it.getCreatedAt().toString()
        );
    }

    @Transactional
    public TransactionResponse handleTransaction(String userMail, String accountNumber, CreateTransactionRequest request)
    {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.ACCOUNT_NOT_FOUND));
        List<Transaction> accountTransactions = transactionRepository.findByAccountId(account.getId());

        validateUser(account.getUser().getEmail(), userMail);
        validateTransaction(accountTransactions, request.type(), request.amount());

        Transaction saved = transactionRepository.save(Transaction.builder()
                                                               .account(account)
                                                               .amount(request.amount())
                                                               .currency(request.currency())
                                                               .reference(request.reference())
                                                               .type(request.type())
                                                               .build());

        return new TransactionResponse(
                saved.getId().toString(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getType(),
                saved.getReference(),
                saved.getAccount().getUser().getId().toString(),
                saved.getCreatedAt().toString()
        );
    }

    private BigDecimal accountAmount(List<Transaction> accountTransactions)
    {
        BigDecimal amount = BigDecimal.ZERO;

        for (Transaction transaction : accountTransactions)
        {
            if ("deposit".equalsIgnoreCase(transaction.getType()))
            {
                amount = amount.add(transaction.getAmount());
            } else
            {
                amount = amount.subtract(transaction.getAmount());
            }
        }

        return amount;
    }

    private void validateTransaction(List<Transaction> accountTransactions, String transactionType, BigDecimal amount)
    {
        if (!"deposit".equals(transactionType) && !"withdrawal".equals(transactionType))
        {
            throw new ApiException(ApiErrorMessage.INVALID_REQUEST);
        }

        if ("withdrawal".equals(transactionType) && amount.compareTo(accountAmount(accountTransactions)) > 0)
        {
            throw new ApiException(ApiErrorMessage.INVALID_REQUEST);
        }
    }

}
