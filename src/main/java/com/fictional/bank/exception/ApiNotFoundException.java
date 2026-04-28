package com.fictional.bank.exception;

public class ApiNotFoundException extends RuntimeException {
    private final ApiErrorMessage errorMessage;

    public ApiNotFoundException(ApiErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ApiErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
