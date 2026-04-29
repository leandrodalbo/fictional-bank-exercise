package com.fictional.bank.request;

import com.fictional.bank.model.AccountType;

public record CreateAccountRequest(String name, AccountType accountType){}
