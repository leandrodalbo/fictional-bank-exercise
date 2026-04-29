package com.fictional.bank.request;

import com.fictional.bank.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(@NotBlank String name, @NotNull AccountType accountType){}
