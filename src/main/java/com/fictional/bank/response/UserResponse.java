package com.fictional.bank.response;

import com.fictional.bank.model.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponse {
    private String id;
    private String name;
    private UserAddress address;
    private String phoneNumber;
    private String email;
    private String createdTimestamp;
    private String updatedTimestamp;
}