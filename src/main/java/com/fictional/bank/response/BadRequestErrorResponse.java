package com.fictional.bank.response;


import java.util.List;

public record BadRequestErrorResponse(
        String message,
        List<ValidationErrorDetail> details
)
{
}