package com.fictional.bank.response;

import com.fictional.bank.entity.UserAddress;

public record UserResponse(
        String id,
        String name,
        UserAddress address,
        String phoneNumber,
        String email,
        String createdTimestamp,
        String updatedTimestamp
) {}