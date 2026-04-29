package com.fictional.bank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType
{
    PERSONAL("personal"),
    OTHER("other");

    private final String value;

    AccountType(String value)
    {
        this.value = value;
    }

    @JsonValue
    public String getValue()
    {
        return value;
    }

    @JsonCreator
    public static AccountType fromValue(String value) {
        for (AccountType type : AccountType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown account type: " + value);
    }
}