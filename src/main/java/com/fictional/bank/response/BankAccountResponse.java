package com.fictional.bank.response;

import java.math.BigDecimal;

public record BankAccountResponse(
        String accountNumber,
        String sortCode,
        String name,
        String accountType,
        BigDecimal balance,
        String currency,
        String createdTimestamp,
        String updatedTimestamp
) {}