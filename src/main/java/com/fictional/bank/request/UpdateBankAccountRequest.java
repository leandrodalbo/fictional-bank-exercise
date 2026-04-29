package com.fictional.bank.request;

public record UpdateBankAccountRequest(
        String name,
        String accountType
)
{
}