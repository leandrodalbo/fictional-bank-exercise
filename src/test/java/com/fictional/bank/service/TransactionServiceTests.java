package com.fictional.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.fictional.bank.TestingUtils.createTransactionRequest;
import static com.fictional.bank.TestingUtils.createWithdrawalRequest;
import static com.fictional.bank.TestingUtils.testingAccount;
import static com.fictional.bank.TestingUtils.transaction;
import static com.fictional.bank.TestingUtils.testingUser;
import static com.fictional.bank.TestingUtils.withdrawalTransaction;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiException;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.response.ListTransactionsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.TransactionRepository;
import com.fictional.bank.response.TransactionResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTests
{

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp()
    {
        transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    @Test
    void shouldHandleDepositTransaction()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionResponse response = transactionService.handleTransaction(testingUser.getEmail(), testingAccount.getAccountNumber(), createTransactionRequest);

        assertEquals("GBP", response.currency());
        assertEquals("deposit", response.type());
        assertNotNull(response.createdTimestamp());


    }

    @Test
    void shouldHandleWithdrawalTransaction()
    {
        testingAccount.setBalance(BigDecimal.valueOf(1000L));
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        when(transactionRepository.save(any())).thenReturn(withdrawalTransaction);

        TransactionResponse response = transactionService.handleTransaction(testingUser.getEmail(), testingAccount.getAccountNumber(), createWithdrawalRequest);

        assertEquals("GBP", response.currency());
        assertEquals("withdrawal", response.type());
        assertNotNull(response.createdTimestamp());
    }

    @Test
    void shouldFailTheWithdrawalTransaction()
    {
        testingAccount.setBalance(BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));

        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> transactionService.handleTransaction(testingUser.getEmail(), testingAccount.getAccountNumber(), createWithdrawalRequest))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());
    }

    @Test
    void shouldFailTheWithInvalidUserEmail()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> transactionService.handleTransaction("invalid@mail.com", testingAccount.getAccountNumber(), createWithdrawalRequest))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());
    }

    @Test
    void shouldFailForANonExistingAccount()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        assertThatExceptionOfType(ApiNotFoundException.class)
                .isThrownBy(() -> transactionService.handleTransaction("invalid@mail.com", testingAccount.getAccountNumber(), createWithdrawalRequest))
                .withMessageContaining(ApiErrorMessage.ACCOUNT_NOT_FOUND.getMessage());
    }

    @Test
    void shouldGetUserTransactions()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        when(transactionRepository.findByAccountId(anyLong())).thenReturn(List.of(transaction));

        ListTransactionsResponse result = transactionService
                .userTransactions(testingUser.getEmail(), testingAccount.getAccountNumber());

        assertThat(result.transactions().isEmpty()).isFalse();

        verify(accountRepository).findByAccountNumber(anyString());
        verify(transactionRepository).findByAccountId(anyLong());
    }

    @Test
    void itIsNastyToCheckSomeoneElseTransactions()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> transactionService.userTransactions("invalid@mail.com", testingAccount.getAccountNumber()))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());

        verify(accountRepository).findByAccountNumber(anyString());
    }

    @Test
    void shouldNotGetTransactionsForInvalidAccounts()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ApiNotFoundException.class)
                .isThrownBy(() -> transactionService.userTransactions("invalid@mail.com", testingAccount.getAccountNumber()))
                .withMessageContaining(ApiErrorMessage.ACCOUNT_NOT_FOUND.getMessage());
    }

    @Test
    void shouldGetUserTransaction()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        when(transactionRepository.findTransaction(anyLong(), anyLong())).thenReturn(Optional.of(transaction));

        TransactionResponse result = transactionService
                .userTransaction(testingUser.getEmail(), testingAccount.getAccountNumber(), 1L);

        assertThat(result).isNotNull();

        verify(accountRepository).findByAccountNumber(anyString());
        verify(transactionRepository).findTransaction(anyLong(), anyLong());
    }

    @Test
    void itIsNastyToCheckSomeoneElseTransaction()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> transactionService.userTransaction("invalid@mail.com", testingAccount.getAccountNumber(), 2L))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());

        verify(accountRepository).findByAccountNumber(anyString());
    }

    @Test
    void shouldNotGetATransactionForInvalidAccounts()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ApiNotFoundException.class)
                .isThrownBy(() -> transactionService.userTransaction("invalid@mail.com", testingAccount.getAccountNumber(), 1L))
                .withMessageContaining(ApiErrorMessage.ACCOUNT_NOT_FOUND.getMessage());
    }


}
