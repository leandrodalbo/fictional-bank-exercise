package com.fictional.bank.request;

import com.fictional.bank.entity.UserAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(

        @NotBlank
        String name,

        @NotNull
        @Valid
        UserAddress address,

        @NotBlank
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
        String phoneNumber,

        @NotBlank
        @Email
        String email
)
{
}