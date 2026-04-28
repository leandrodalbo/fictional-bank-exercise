package com.fictional.bank.exception;

public class ApiException extends RuntimeException {
    private final ApiErrorMessage errorMessage;

    public ApiException(ApiErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ApiErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
