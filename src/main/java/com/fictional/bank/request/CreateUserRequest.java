package com.fictional.bank.request;

import com.fictional.bank.model.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserRequest {
    private String name;
    private UserAddress address;
    private String phoneNumber;
    private String email;
}