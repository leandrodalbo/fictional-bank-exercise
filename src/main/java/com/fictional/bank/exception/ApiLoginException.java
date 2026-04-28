package com.fictional.bank.exception;

public class ApiLoginException extends RuntimeException {
    private final ApiErrorMessage errorMessage;

    public ApiLoginException(ApiErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ApiErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
