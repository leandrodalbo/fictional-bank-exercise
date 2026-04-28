package com.fictional.bank.exception;

public class ApiNotDeletableException extends RuntimeException {
    private final ApiErrorMessage errorMessage;

    public ApiNotDeletableException(ApiErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ApiErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
