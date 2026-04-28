package com.fictional.bank.request;


import java.util.Optional;

import com.fictional.bank.model.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequest
{
    private Optional<String> name;
    private Optional<UserAddress> address;
    private Optional<String> phoneNumber;
    private Optional<String> email;
}