package com.fictional.bank.request;

import com.fictional.bank.entity.UserAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(

        String name,

        @Valid
        UserAddress address,

        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
        String phoneNumber,

        @Email
        String email
)
{
}