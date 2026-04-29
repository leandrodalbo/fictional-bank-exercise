package com.fictional.bank.utils;

import com.fictional.bank.exception.ApiErrorMessage;
import org.springframework.security.access.AccessDeniedException;

public class Utils
{
    public static void validateUser(String userEmail, String currentUserEmail)
    {
        if (!userEmail.equals(currentUserEmail))
            throw new AccessDeniedException(ApiErrorMessage.INVALID_REQUEST.getMessage());
    }
}
