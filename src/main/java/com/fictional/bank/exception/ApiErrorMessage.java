package com.fictional.bank.exception;

public enum ApiErrorMessage {
    USER_NOT_FOUND("User not found."),
    ACCOUNT_NOT_FOUND("User not found."),
    EMAIL_ALREADY_EXISTS("Email already exists."),
    INVALID_REQUEST("Invalid request data."),
    INTERNAL_ERROR("An unexpected error occurred.");

    private final String message;

    ApiErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
