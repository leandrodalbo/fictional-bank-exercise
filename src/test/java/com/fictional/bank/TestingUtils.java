package com.fictional.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fictional.bank.entity.Account;
import com.fictional.bank.entity.Transaction;
import com.fictional.bank.entity.User;
import com.fictional.bank.entity.UserAddress;
import com.fictional.bank.request.CreateTransactionRequest;
import com.fictional.bank.request.UpdateBankAccountRequest;
import com.fictional.bank.request.UpdateUserRequest;
import com.fictional.bank.utils.Utils;

public class TestingUtils
{
    public static final String secret = "uQw8vQ1pQ2t6bXJ5dGZzZ2hqa2xtbm9wcXJzdHV2d3h5eg==";
    public static final Long expiration = 86400000L;

    public static final User testingUser = new User(
            1001L,
            "user-name",
            new UserAddress("l1", "l2", "l3", "town", "", ""),
            "testinguser@mail.com",
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    public static final User updatedUser = new User(
            1001L,
            "user-name",
            new UserAddress("l1", "l2", "l3", "town", "", ""),
            "updated@mail.com",
            "+5445350001",
            LocalDateTime.now(),
            LocalDateTime.now()
    );


    public static final UpdateUserRequest updateUserRequest = new UpdateUserRequest("updating-name", new UserAddress("l1", "l2", "l3", "town", "", ""), "+4498216847", "updated@mail.com");


    public static final Account testingAccount = Account.builder()
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

    public static final Transaction transaction = Transaction.builder()
            .account(testingAccount)
            .id(10L)
            .createdAt(LocalDateTime.now())
            .amount(BigDecimal.valueOf(50))
            .currency("GBP")
            .reference("ref")
            .type("deposit")
            .build();

    public static final Transaction withdrawalTransaction = Transaction.builder()
            .account(testingAccount)
            .id(10L)
            .createdAt(LocalDateTime.now())
            .amount(BigDecimal.valueOf(50))
            .currency("GBP")
            .reference("ref")
            .type("withdrawal")
            .build();





    public static final UpdateBankAccountRequest updateBankAccountRequest = new UpdateBankAccountRequest(
            "UPDATED-NAME", "OTHER-TYPE"
    );

    public static final CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest(new BigDecimal("100.00"), "GBP", "deposit", "ref");
    public static final CreateTransactionRequest createWithdrawalRequest = new CreateTransactionRequest(new BigDecimal("30.00"), "GBP", "withdrawal", "ref");

}
