package com.fictional.bank.response;

import java.math.BigDecimal;

public record TransactionResponse(
        String id,
        BigDecimal amount,
        String currency,
        String type,
        String reference,
        String userId,
        String createdTimestamp
) {}