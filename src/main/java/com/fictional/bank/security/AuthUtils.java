package com.fictional.bank.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils
{

    public String getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();

    }
}
