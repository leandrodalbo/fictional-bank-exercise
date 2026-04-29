package com.fictional.bank.response;



public record ValidationErrorDetail(
        String field,
        String message,
        String type
) {}