package com.fictional.bank.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email){}
