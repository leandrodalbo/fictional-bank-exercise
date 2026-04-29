package com.fictional.bank.utils;

import com.fictional.bank.exception.ApiErrorMessage;
import org.springframework.security.access.AccessDeniedException;

public class Utils
{
    public static final String PERSONAL_ACCOUNT_TYPE = "personal";
    public static final String GBP_CURRENCY = "GBP";

    public static void validateUser(String userEmail, String currentUserEmail)
    {
        if (!userEmail.equals(currentUserEmail))
            throw new AccessDeniedException(ApiErrorMessage.INVALID_REQUEST.getMessage());
    }
}
