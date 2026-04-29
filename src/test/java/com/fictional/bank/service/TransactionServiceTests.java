package com.fictional.bank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fictional.bank.entity.UserAddress;
import com.fictional.bank.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import static org.mockito.Mockito.when;

import com.fictional.bank.entity.Account;
import com.fictional.bank.entity.Transaction;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.TransactionRepository;
import com.fictional.bank.request.CreateTransactionRequest;
import com.fictional.bank.response.TransactionResponse;
import com.fictional.bank.entity.User;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTests
{

    private final User testingUser = new User(
            1001L,
            "user-name",
            new UserAddress("l1", "l2", "l3", "town", "", ""),
            "testinguser@mail.com",
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final Account testingAccount = Account.builder()
            .id(1L)
            .user(testingUser)
            .accountNumber("12345678")
            .sortCode("12-34-56")
            .accountName("Personal Account")
            .accountType("personal")
            .balance(BigDecimal.ZERO)
            .currency(Utils.GBP_CURRENCY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

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
        CreateTransactionRequest req = new CreateTransactionRequest(new BigDecimal("100.00"), "GBP", "deposit", "ref");

        Transaction transaction = Transaction.builder()
                .account(testingAccount)
                .amount(req.amount())
                .currency(req.currency())
                .reference(req.reference())
                .type(req.type())
                .build();
        transaction.setId(10L);
        transaction.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        when(transactionRepository.findByAccountId(anyLong())).thenReturn(List.of());
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionResponse response = transactionService.handleTransaction(testingUser.getEmail(), testingAccount.getAccountNumber(), req);

        assertEquals("10", response.id());
        assertEquals(new BigDecimal("100.00"), response.amount());
        assertEquals("GBP", response.currency());
        assertEquals("deposit", response.type());
        assertEquals("ref", response.reference());
        assertNotNull(response.createdTimestamp());
    }

}
