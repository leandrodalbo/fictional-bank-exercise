package com.fictional.bank.response;

public record AccountResponse(
        String accountNumber,
        String sortCode,
        String name,
        String accountType,
        String balance,
        String currency,
        String createdTimestamp,
        String updatedTimestamp
)
{
}