package com.fictional.bank.request;


import com.fictional.bank.model.UserAddress;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank
    private String name;

    @NotNull
    @Valid
    private UserAddress address;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;
}