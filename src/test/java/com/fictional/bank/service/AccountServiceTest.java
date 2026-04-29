package com.fictional.bank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fictional.bank.model.Account;
import com.fictional.bank.model.AccountType;
import com.fictional.bank.model.Currency;
import com.fictional.bank.model.User;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest
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
            .currency(Currency.GBP.getValue())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();


    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp()
    {
        accountService = new AccountService(accountRepository, userRepository);
    }


    @Test
    void shouldCreateANewUser()
    {

        CreateAccountRequest request = new CreateAccountRequest("new account", AccountType.PERSONAL);

        when(userRepository.findByEmail(testingUser.getEmail())).thenReturn(testingUser);
        when(accountRepository.save(any())).thenReturn(testingAccount);

        assertThat(accountService.createNewAccount(testingUser.getEmail(), request)).isNotNull();

        verify(userRepository).findByEmail(testingUser.getEmail());
        verify(accountRepository).save(any());
    }


}
