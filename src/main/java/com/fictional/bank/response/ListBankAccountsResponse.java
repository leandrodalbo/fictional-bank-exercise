package com.fictional.bank.response;

import java.util.List;

public record ListBankAccountsResponse(
        List<BankAccountResponse> accounts
) {}