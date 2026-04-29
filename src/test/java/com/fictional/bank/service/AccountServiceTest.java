package com.fictional.bank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.model.Account;
import com.fictional.bank.model.User;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateBankAccountRequest;
import com.fictional.bank.request.UpdateBankAccountRequest;
import com.fictional.bank.response.BankAccountResponse;
import com.fictional.bank.response.ListBankAccountsResponse;
import com.fictional.bank.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
            .currency(Utils.GBP_CURRENCY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final UpdateBankAccountRequest updateBankAccountRequest = new UpdateBankAccountRequest(
            "UPDATED-NAME", "OTHER-TYPE"
    );


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
        CreateBankAccountRequest request = new CreateBankAccountRequest("new account", Utils.PERSONAL_ACCOUNT_TYPE);

        when(userRepository.findByEmail(testingUser.getEmail())).thenReturn(testingUser);
        when(accountRepository.save(any())).thenReturn(testingAccount);

        assertThat(accountService.createNewAccount(testingUser.getEmail(), request)).isNotNull();

        verify(userRepository).findByEmail(testingUser.getEmail());
        verify(accountRepository).save(any());
    }

    @Test
    void shouldGetUserAccountsDetails()
    {
        when(userRepository.findByEmail(anyString())).thenReturn(testingUser);
        when(accountRepository.findByUserId(anyLong())).thenReturn(Set.of(testingAccount));

        ListBankAccountsResponse result = accountService.userAccountsDetails(testingUser.getEmail());

        assertThat(result.accounts().isEmpty()).isFalse();

        verify(userRepository).findByEmail(anyString());
        verify(accountRepository).findByUserId(anyLong());
    }

    @Test
    void shouldGetUserSingleAccountDetails()
    {

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));

        BankAccountResponse result = accountService.userAccountDetails(testingUser.getEmail(), testingAccount.getAccountNumber());

        assertThat(result).isNotNull();

        verify(accountRepository).findByAccountNumber(anyString());
    }


    @Test
    void shouldUpdateUserAccountDetails()
    {

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        when(accountRepository.save(any())).thenReturn(testingAccount);

        BankAccountResponse response = accountService.updateUserAccountDetails(testingUser.getEmail(), testingAccount.getAccountNumber(), updateBankAccountRequest);

        assertThat(response.accountType()).isEqualTo(updateBankAccountRequest.accountType());

        verify(accountRepository).save(any());
        verify(accountRepository).findByAccountNumber(anyString());
    }

    @Test
    void shouldNotUpdateForAccountNotFound()
    {

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ApiNotFoundException.class)
                .isThrownBy(() -> accountService.updateUserAccountDetails(testingUser.getEmail(), testingAccount.getAccountNumber(), updateBankAccountRequest))
                .withMessageContaining(ApiErrorMessage.ACCOUNT_NOT_FOUND.getMessage());


        verify(accountRepository).findByAccountNumber(anyString());
    }


    @Test
    void shouldNotUpdateTheDetailsOfAnotherUser()
    {
        String email = "userMail@mail.com";

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> accountService.updateUserAccountDetails(email, testingAccount.getAccountNumber(), updateBankAccountRequest))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());


        verify(accountRepository).findByAccountNumber(anyString());
    }

    @Test
    void shouldDeleteUserAccountDetails()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));
        doNothing().when(accountRepository).deleteById(anyLong());

        accountService.deleteUserAccount(testingUser.getEmail(), testingAccount.getAccountNumber());


        verify(accountRepository).findByAccountNumber(anyString());
        verify(accountRepository).deleteById(anyLong());

    }

    @Test
    void shouldNotDeleteAnAccountWhenNotFound()
    {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ApiNotFoundException.class)
                .isThrownBy(() -> accountService.deleteUserAccount(testingUser.getEmail(), testingAccount.getAccountNumber()))
                .withMessageContaining(ApiErrorMessage.ACCOUNT_NOT_FOUND.getMessage());

        verify(accountRepository).findByAccountNumber(anyString());
    }


    @Test
    void shouldNotDeleteAnotherUserAccount()
    {
        String email = "userMail@mail.com";
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(testingAccount));

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> accountService.deleteUserAccount(email, testingAccount.getAccountNumber()))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());

        verify(accountRepository).findByAccountNumber(anyString());
    }


}
