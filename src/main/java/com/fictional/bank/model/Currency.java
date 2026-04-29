package com.fictional.bank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency
{
    GBP("gbp"),
    USD("usd");

    private final String value;

    Currency(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }
}