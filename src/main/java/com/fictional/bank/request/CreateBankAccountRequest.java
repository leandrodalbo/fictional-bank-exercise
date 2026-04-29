package com.fictional.bank.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBankAccountRequest(

        @NotBlank
        String name,

        @NotBlank
        String accountType
)
{
}